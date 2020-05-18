package ch.epfl.favo.chat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.util.ui.ImeHelper;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
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
import ch.epfl.favo.util.PictureUtil;

import static ch.epfl.favo.util.CommonTools.hideSoftKeyboard;

@SuppressLint("NewApi")
public class ChatPage extends Fragment {

  private static String TAG = "ChatPage";

  private View view;
  private RecyclerView recyclerView;
  private Favor currentFavor;

  private static String FAVOR_ID = "favorId";
  private static String NOTIF_ID = "notifId";
  private static String USER_ID = "uid";
  private static int FILE_CHOOSER_RC = 1;

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    view = inflater.inflate(R.layout.fragment_chat, container, false);
    requireActivity()
        .getWindow()
        .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

    if (getArguments() != null) {
      currentFavor = getArguments().getParcelable(CommonTools.FAVOR_ARGS);
    }
    setupView();

    return view;
  }

  @SuppressLint({"ClickableViewAccessibility", "RestrictedApi"})
  private void setupView() {

    ((MainActivity) requireActivity()).hideBottomNavigation();

    ImageButton sendMessageButton = view.findViewById(R.id.sendButton);

    sendMessageButton.setOnClickListener(v -> onSendClick());
    ImageButton sendPictureButton = view.findViewById(R.id.send_image_button);
    sendPictureButton.setOnClickListener(v -> onSendImageClick());

    LinearLayoutManager manager = new LinearLayoutManager(getContext());
    manager.setReverseLayout(true);
    manager.setStackFromEnd(true);

    recyclerView = view.findViewById(R.id.messagesList);
    recyclerView.setHasFixedSize(true);
    recyclerView.setLayoutManager(manager);

    recyclerView.addOnLayoutChangeListener(
        (view, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
          if (bottom < oldBottom) {
            recyclerView.postDelayed(() -> recyclerView.smoothScrollToPosition(0), 100);
          }
        });

    recyclerView.setOnTouchListener(
        (v, event) -> {
          hideSoftKeyboard(requireActivity());
          return false;
        });

    ImeHelper.setImeOnDoneListener(view.findViewById(R.id.messageEdit), this::onSendClick);
    setupToolBar();
  }

  private void onSendImageClick() {
    Intent imageIntent = new Intent().setType("image/*").setAction(Intent.ACTION_GET_CONTENT);
    startActivityForResult(Intent.createChooser(imageIntent, "Select Image"), FILE_CHOOSER_RC);
  }

  private void setupToolBar() {
    Toolbar toolbar = requireActivity().findViewById(R.id.toolbar_main_activity);
    toolbar.setBackgroundColor(getResources().getColor(R.color.material_green_500));
    toolbar.setTitleTextColor(Color.WHITE);
    Objects.requireNonNull(toolbar.getNavigationIcon())
        .setColorFilter(new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP));
    toolbar.setTitle(currentFavor.getTitle());
  }

  @Override
  public void onStart() {
    super.onStart();
    attachRecyclerViewAdapter();
  }

  private void attachRecyclerViewAdapter() {
    FirestoreRecyclerOptions<Message> options = getFirestoreRecyclerOptions();
    final RecyclerView.Adapter adapter = createRecyclerAdapter(options);

    RecyclerView mRecyclerView = view.findViewById(R.id.messagesList);

    adapter.registerAdapterDataObserver(
        new RecyclerView.AdapterDataObserver() {
          @Override
          public void onItemRangeInserted(int positionStart, int itemCount) {
            mRecyclerView.postDelayed(() -> mRecyclerView.smoothScrollToPosition(0), 100);
          }
        });

    mRecyclerView.setAdapter(adapter);
  }
  /*
  Sends text message
   */
  private void onSendClick() {
    Message message = generateMessageFromView(CommonTools.TEXT_MESSAGE_TYPE);
    onAddMessage(message);
    ((EditText) view.findViewById(R.id.messageEdit)).setText("");
  }

  private Message generateMessageFromView(String messageType) {
    String favorId = currentFavor.getId();
    String requesterNotifId = currentFavor.getRequesterNotifId();
    String responderUserId = DependencyFactory.getCurrentFirebaseUser().getUid();
    // If the empty message view is visible, then this is the first message.
    String isFirstMsg =
        String.valueOf(view.findViewById(R.id.emptyTextView).getVisibility() == View.VISIBLE);
    EditText mMessageEdit = view.findViewById(R.id.messageEdit);
    return new Message(
        DependencyFactory.getCurrentFirebaseUser().getDisplayName(),
        responderUserId,
        messageType,
        mMessageEdit.getText().toString(),
        null,
        requesterNotifId,
        favorId,
        isFirstMsg);
  }

  private FirestoreRecyclerOptions<Message> getFirestoreRecyclerOptions() {
    Query sChatQuery =
        FirebaseFirestore.getInstance()
            .collection("chats")
            .whereEqualTo("favorId", currentFavor.getId())
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(50);

    return new FirestoreRecyclerOptions.Builder<Message>()
        .setQuery(sChatQuery, Message.class)
        .setLifecycleOwner(this)
        .build();
  }

  @NonNull
  private RecyclerView.Adapter createRecyclerAdapter(FirestoreRecyclerOptions<Message> options) {
    return new FirestoreRecyclerAdapter<Message, MessageViewHolder>(options) {
      private int TEXT_TYPE = 0;
      private int IMG_TYPE = 1;

      @Override
      public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

      }

      @Override
      public int getItemViewType(int position) {
        return (getItem(position).getMessageType().equals(CommonTools.TEXT_MESSAGE_TYPE))?0:1;
      }

      @NonNull
      @Override
      public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View messageView;
        MessageViewHolder viewHolder;
        if (viewType==IMG_TYPE) {
          messageView =
              LayoutInflater.from(parent.getContext())
                  .inflate(R.layout.chat_image_message, parent, false);
          viewHolder = new ImageMessageViewHolder(messageView);
        } else { // Text message
          messageView =
              LayoutInflater.from(parent.getContext())
                  .inflate(R.layout.chat_text_message, parent, false);
          viewHolder = new TextMessageViewHolder(messageView);
        }

        messageView.setOnClickListener(
            v -> {
              int itemPosition = recyclerView.getChildLayoutPosition(v);
              Message model = getItem(itemPosition);

              Bundle userBundle = new Bundle();
              userBundle.putString("USER_ARGS", model.getUid());
              Navigation.findNavController(requireView())
                  .navigate(R.id.action_nav_chatView_to_UserInfoPage, userBundle);
            });

        return viewHolder;
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

  private Task<DocumentReference> onAddMessage(@NonNull Message message) {
    return FirebaseFirestore.getInstance()
        .collection("chats")
        .add(message)
        .addOnFailureListener(
            requireActivity(),
            e ->
                Toast.makeText(
                        getContext(),
                        "Failed to send message. Check your internet connection.",
                        Toast.LENGTH_SHORT)
                    .show());
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
        PictureUtil.getInstance()
            .uploadPicture(selectedImage)
            .thenAccept(
                remotePath -> {
                  Message imageMessage = generateMessageFromView(CommonTools.IMAGE_MESSAGE_TYPE);
                  imageMessage.setPicturePath(remotePath);
                  onAddMessage(imageMessage)
                      .addOnFailureListener(
                          e -> CommonTools.showSnackbar(requireView(), "Failed uploading picture"));
                });
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
