package ch.epfl.favo.chat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.util.ui.ImeHelper;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.Query;

import java.io.IOException;
import java.util.Objects;

import ch.epfl.favo.MainActivity;
import ch.epfl.favo.R;
import ch.epfl.favo.chat.Model.Message;
import ch.epfl.favo.chat.ViewHolder.ImageMessageViewHolder;
import ch.epfl.favo.chat.ViewHolder.MessageViewHolder;
import ch.epfl.favo.chat.ViewHolder.TextMessageViewHolder;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.util.CommonTools;
import ch.epfl.favo.util.DependencyFactory;
import ch.epfl.favo.util.IPictureUtil;
import ch.epfl.favo.view.tabs.MapPage;
import ch.epfl.favo.viewmodel.IFavorViewModel;

import static ch.epfl.favo.util.CommonTools.IMAGE_MESSAGE_TYPE;
import static ch.epfl.favo.util.CommonTools.LOCATION_MESSAGE_TYPE;
import static ch.epfl.favo.util.CommonTools.TEXT_MESSAGE_TYPE;
import static ch.epfl.favo.util.CommonTools.hideSoftKeyboard;

@SuppressLint("NewApi")
public class ChatPage extends Fragment {

  private static final String DEFAULT_TOOLBAR_TITLE = "Favor Title";
  private static final String TAG = "ChatPage";
  private static final int FILE_CHOOSER_RC = 1;
  private static final int SCROLL_DELAY = 200;
  private static final String IMAGE_PATH = "image/*";

  private View view;
  private RecyclerView recyclerView;
  private Favor currentFavor;
  private IFavorViewModel viewModel;

  private LatLng locationForMessage;
  private IPictureUtil pictureUtil;
  private IChatUtil chatUtil;

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    view = inflater.inflate(R.layout.fragment_chat, container, false);
    requireActivity()
        .getWindow()
        .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    viewModel =
        (IFavorViewModel)
            new ViewModelProvider(requireActivity())
                .get(DependencyFactory.getCurrentViewModelClass());

    //    currentFavor = viewModel.getObservedFavor().getValue();
    viewModel
        .getObservedFavor()
        .observe(
            getViewLifecycleOwner(),
            favor -> {
              if (favor != null) {
                currentFavor = favor;
                setupToolBar();
                attachRecyclerViewAdapter();
              }
            });

    if (getArguments() != null) {
      locationForMessage = (getArguments().getParcelable(MapPage.LOCATION_ARGUMENT_KEY));
    }

    setupView();
    pictureUtil = DependencyFactory.getCurrentPictureUtility();
    chatUtil = DependencyFactory.getCurrentChatUtility();
    return view;
  }

  @SuppressLint({"ClickableViewAccessibility", "RestrictedApi"})
  private void setupView() {

    ((MainActivity) requireActivity()).hideBottomNavigation();
    setupButtons();

    LinearLayoutManager manager = new LinearLayoutManager(getContext());
    manager.setReverseLayout(true);
    manager.setStackFromEnd(true);

    recyclerView = view.findViewById(R.id.messagesList);
    recyclerView.setHasFixedSize(true);
    recyclerView.setLayoutManager(manager);

    recyclerView.addOnLayoutChangeListener(
        (view, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
          if (bottom < oldBottom) {
            recyclerView.postDelayed(() -> recyclerView.smoothScrollToPosition(0), SCROLL_DELAY);
          }
        });

    recyclerView.setOnTouchListener(
        (v, event) -> {
          hideSoftKeyboard(requireActivity());
          return false;
        });

    ImeHelper.setImeOnDoneListener(view.findViewById(R.id.messageEdit), this::onSendClick);
  }

  private void setupButtons() {
    ImageButton sendMessageButton = view.findViewById(R.id.sendButton);
    sendMessageButton.setOnClickListener(v -> onSendClick());
    ImageButton sendPictureButton = view.findViewById(R.id.send_image_button);
    sendPictureButton.setOnClickListener(v -> onSendImageClick());
    ImageButton shareLocationButton = view.findViewById(R.id.share_location_button);
    shareLocationButton.setOnClickListener(v -> onShareLocationClick());
  }

  private void onShareLocationClick() {
    new AlertDialog.Builder(requireActivity())
        .setMessage(getText(R.string.share_location_message))
        .setPositiveButton(
            R.string.share_location_propose,
            (dialog, which) -> {
              viewModel.setShowObservedFavor(false);
              Bundle locationBundle = new Bundle();
              locationBundle.putInt(MapPage.LOCATION_ARGUMENT_KEY, MapPage.SHARE_LOCATION);
              Navigation.findNavController(requireView())
                  .navigate(R.id.action_chatView_to_nav_map, locationBundle);
            })
        .setNegativeButton(
            R.string.share_location_current,
            (dialog, which) -> {
              Location currentLoc =
                  DependencyFactory.getCurrentGpsTracker(requireActivity().getApplicationContext())
                      .getLocation();
              locationForMessage = new LatLng(currentLoc.getLatitude(), currentLoc.getLongitude());
              shareLocationMessage(locationForMessage);
            })
        .show();
  }

  private void shareLocationMessage(LatLng locationForMessage) {
    Message locationMessage = generateMessageFromView(LOCATION_MESSAGE_TYPE);
    locationMessage.setPicturePath(
        chatUtil.generateGoogleMapsPath(locationForMessage.latitude, locationForMessage.longitude));
    locationMessage.setLongitude(String.valueOf(locationForMessage.longitude));
    locationMessage.setLatitude(String.valueOf(locationForMessage.latitude));
    chatUtil.addChatMessage(locationMessage).exceptionally(this::handleException);
  }

  private void onSendImageClick() {
    Intent imageIntent = new Intent().setType(IMAGE_PATH).setAction(Intent.ACTION_GET_CONTENT);
    startActivityForResult(
        Intent.createChooser(imageIntent, getString(R.string.select_image_text)), FILE_CHOOSER_RC);
  }

  private void setupToolBar() {
    Toolbar toolbar = requireActivity().findViewById(R.id.toolbar_main_activity);
    toolbar.setBackgroundColor(getResources().getColor(R.color.material_green_500));
    toolbar.setTitleTextColor(Color.WHITE);
    Objects.requireNonNull(toolbar.getNavigationIcon())
        .setColorFilter(new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP));
    toolbar.setTitle(currentFavor != null ? currentFavor.getTitle() : DEFAULT_TOOLBAR_TITLE);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    if (locationForMessage != null) {
      shareLocationMessage(locationForMessage);
      locationForMessage = null; // reset its value
    }
  }

  @Override
  public void onStart() {
    super.onStart();
    //    attachRecyclerViewAdapter();
  }

  private void attachRecyclerViewAdapter() {
    FirestoreRecyclerOptions<Message> options = getFirestoreRecyclerOptions();
    final RecyclerView.Adapter adapter = createRecyclerAdapter(options);

    RecyclerView mRecyclerView = view.findViewById(R.id.messagesList);

    adapter.registerAdapterDataObserver(
        new RecyclerView.AdapterDataObserver() {
          @Override
          public void onItemRangeInserted(int positionStart, int itemCount) {
            mRecyclerView.postDelayed(() -> mRecyclerView.smoothScrollToPosition(0), SCROLL_DELAY);
          }
        });

    mRecyclerView.setAdapter(adapter);
  }

  // Sends text message
  private void onSendClick() {
    Message message = generateMessageFromView(CommonTools.TEXT_MESSAGE_TYPE);
    chatUtil.addChatMessage(message).exceptionally(this::handleException);
    ((EditText) view.findViewById(R.id.messageEdit)).setText("");
  }

  private Message generateMessageFromView(int messageType) {
    String favorId = currentFavor.getId();
    String responderUserId = DependencyFactory.getCurrentFirebaseUser().getUid();
    EditText mMessageEdit = view.findViewById(R.id.messageEdit);
    return new Message(
        DependencyFactory.getCurrentFirebaseUser().getDisplayName(),
        responderUserId,
        messageType,
        mMessageEdit.getText().toString(),
        null,
        favorId);
  }

  private FirestoreRecyclerOptions<Message> getFirestoreRecyclerOptions() {
    Query sChatQuery =
        ChatUtil.getSingleInstance().getAllChatMessagesForFavor(currentFavor.getId());

    return new FirestoreRecyclerOptions.Builder<Message>()
        .setQuery(sChatQuery, Message.class)
        .setLifecycleOwner(this)
        .build();
  }

  @NonNull
  private RecyclerView.Adapter createRecyclerAdapter(FirestoreRecyclerOptions<Message> options) {
    return new FirestoreRecyclerAdapter<Message, MessageViewHolder>(options) {

      @Override
      public int getItemViewType(int position) {
        return getItem(position).getMessageType();
      }

      @NonNull
      @Override
      public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View messageView;
        MessageViewHolder viewHolder;

        if (viewType == TEXT_MESSAGE_TYPE) {
          messageView =
              LayoutInflater.from(parent.getContext())
                  .inflate(R.layout.chat_text_message, parent, false);
          viewHolder = new TextMessageViewHolder(messageView);
        } else { // Image or location
          messageView =
              LayoutInflater.from(parent.getContext())
                  .inflate(R.layout.chat_image_message, parent, false);
          viewHolder = new ImageMessageViewHolder(messageView);
        }

        if (viewType == LOCATION_MESSAGE_TYPE)
          messageView.setOnClickListener(this::navigateToMapPage);
        else messageView.setOnClickListener(this::navigateToUserPage);

        return viewHolder;
      }

      private void navigateToMapPage(View v) {
        int itemPosition = recyclerView.getChildLayoutPosition(v);
        Message model = getItem(itemPosition);
        Bundle mapBundle = new Bundle();
        mapBundle.putString(MapPage.LATITUDE_ARGUMENT_KEY, model.getLatitude());
        mapBundle.putString(MapPage.LONGITUDE_ARGUMENT_KEY, model.getLongitude());
        mapBundle.putInt(MapPage.LOCATION_ARGUMENT_KEY, MapPage.OBSERVE_LOCATION);
        Navigation.findNavController(requireView()).navigate(R.id.action_global_nav_map, mapBundle);
      }

      private void navigateToUserPage(View v) {
        int itemPosition = recyclerView.getChildLayoutPosition(v);
        Message model = getItem(itemPosition);

        Bundle userBundle = new Bundle();
        userBundle.putString(CommonTools.USER_ARGS, model.getUid());
        CommonTools.hideSoftKeyboard(requireActivity());
        Navigation.findNavController(requireView())
            .navigate(R.id.action_nav_chatView_to_UserInfoPage, userBundle);
      }

      @Override
      public void onDataChanged() {
        view.findViewById(R.id.emptyTextView)
            .setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
      }

      @Override
      protected void onBindViewHolder(
          @NonNull MessageViewHolder holder, int position, @NonNull Message model) {
        holder.bind(model);
      }
    };
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    if (requestCode == FILE_CHOOSER_RC
        && resultCode == Activity.RESULT_OK
        && data != null
        && data.getData() != null) {
      Uri imagePath = data.getData();
      try {
        Bitmap selectedImage =
            ImageDecoder.decodeBitmap(
                ImageDecoder.createSource(requireActivity().getContentResolver(), imagePath));
        pictureUtil
            .uploadPicture(selectedImage)
            .thenAccept(
                remotePath -> {
                  Message imageMessage = generateMessageFromView(IMAGE_MESSAGE_TYPE);
                  imageMessage.setPicturePath(remotePath);
                  chatUtil.addChatMessage(imageMessage).exceptionally(this::handleException);
                });
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private Void handleException(Throwable throwable) {
    CommonTools.showSnackbar(requireView(), getString(R.string.error_send_msg_chat));
    Log.e(TAG, throwable.toString());
    return null;
  }
}
