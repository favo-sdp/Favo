package ch.epfl.favo.chat;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.util.ui.ImeHelper;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Objects;

import ch.epfl.favo.MainActivity;
import ch.epfl.favo.R;
import ch.epfl.favo.favor.Favor;
import ch.epfl.favo.util.DependencyFactory;
import ch.epfl.favo.util.FavorFragmentFactory;

import static ch.epfl.favo.util.CommonTools.hideSoftKeyboard;

public class ChatPage extends Fragment {

  private static final CollectionReference sChatCollection =
      FirebaseFirestore.getInstance().collection("chats");

  private View view;
  private Favor favor;

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    view = inflater.inflate(R.layout.fragment_chat, container, false);

    setupView();

    if (getArguments() != null) {
      favor = getArguments().getParcelable(FavorFragmentFactory.FAVOR_ARGS);
    }

    return view;
  }

  @SuppressLint({"ClickableViewAccessibility", "RestrictedApi"})
  private void setupView() {

    ((MainActivity) requireActivity()).hideBottomNavigation();

    Button sendMessageButton = view.findViewById(R.id.sendButton);
    sendMessageButton.setOnClickListener(v -> onSendClick());

    LinearLayoutManager manager = new LinearLayoutManager(getContext());
    manager.setReverseLayout(true);
    manager.setStackFromEnd(true);

    RecyclerView mRecyclerView = view.findViewById(R.id.messagesList);
    mRecyclerView.setHasFixedSize(true);
    mRecyclerView.setLayoutManager(manager);

    mRecyclerView.addOnLayoutChangeListener(
        (view, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
          if (bottom < oldBottom) {
            mRecyclerView.postDelayed(() -> mRecyclerView.smoothScrollToPosition(0), 100);
          }
        });

    mRecyclerView.setOnTouchListener(
        (v, event) -> {
          hideSoftKeyboard(requireActivity());
          return false;
        });

    ImeHelper.setImeOnDoneListener(view.findViewById(R.id.messageEdit), this::onSendClick);
  }

  @Override
  public void onStart() {
    super.onStart();
    attachRecyclerViewAdapter();
    Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
    toolbar.setBackgroundColor(getResources().getColor(R.color.material_green_500));
    toolbar.setTitleTextColor(Color.WHITE);
    Objects.requireNonNull(toolbar.getNavigationIcon())
        .setColorFilter(new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP));
    toolbar.setTitle(favor.getTitle());
  }

  @Override
  public void onStop() {
    super.onStop();
    Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
    toolbar.setBackgroundColor(Color.TRANSPARENT);
    toolbar.setTitleTextColor(Color.BLACK);
    Objects.requireNonNull(toolbar.getNavigationIcon())
        .setColorFilter(new PorterDuffColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP));
    toolbar.setTitle("");
  }

  private void attachRecyclerViewAdapter() {
    FirestoreRecyclerOptions<ChatModel> options = getFirestoreRecyclerOptions();
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

  private void onSendClick() {
    EditText mMessageEdit = view.findViewById(R.id.messageEdit);
    onAddMessage(
        new ChatModel(
            DependencyFactory.getCurrentFirebaseUser().getDisplayName(),
            mMessageEdit.getText().toString(),
            DependencyFactory.getCurrentFirebaseUser().getUid(),
            favor.getId()));
    mMessageEdit.setText("");
  }

  private FirestoreRecyclerOptions<ChatModel> getFirestoreRecyclerOptions() {
    Query sChatQuery =
        sChatCollection
            .whereEqualTo("favorId", favor.getId())
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(50);

    return new FirestoreRecyclerOptions.Builder<ChatModel>()
        .setQuery(sChatQuery, ChatModel.class)
        .setLifecycleOwner(this)
        .build();
  }

  @NonNull
  private RecyclerView.Adapter createRecyclerAdapter(FirestoreRecyclerOptions<ChatModel> options) {
    return new FirestoreRecyclerAdapter<ChatModel, ChatViewHolder>(options) {
      @NonNull
      @Override
      public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatViewHolder(
            LayoutInflater.from(parent.getContext()).inflate(R.layout.message, parent, false));
      }

      @Override
      protected void onBindViewHolder(
          @NonNull ChatViewHolder holder, int position, @NonNull ChatModel model) {
        holder.bind(model);
      }

      @Override
      public void onDataChanged() {
        view.findViewById(R.id.emptyTextView)
            .setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
      }
    };
  }

  private void onAddMessage(@NonNull ChatModel chatModel) {
    sChatCollection
        .add(chatModel)
        .addOnFailureListener(
            requireActivity(),
            e ->
                Toast.makeText(
                        getContext(),
                        "Failed to send message. Check your internet connection.",
                        Toast.LENGTH_SHORT)
                    .show());
  }
}
