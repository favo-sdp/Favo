package ch.epfl.favo.chat;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Objects;

import ch.epfl.favo.R;
import ch.epfl.favo.view.ViewController;

public class ChatPage extends Fragment {

  private static final CollectionReference sChatCollection =
      FirebaseFirestore.getInstance().collection("chats");

  private static final Query sChatQuery =
      sChatCollection.orderBy("timestamp", Query.Direction.DESCENDING).limit(50);

  private View view;

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    view = inflater.inflate(R.layout.fragment_chat, container, false);
    setupView();
    return view;
  }

  private void setupView() {

    ((ViewController) Objects.requireNonNull(getActivity())).setupViewBotDestTab();

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
  }

  @Override
  public void onStart() {
    super.onStart();
    attachRecyclerViewAdapter();
  }

  private void attachRecyclerViewAdapter() {
    final RecyclerView.Adapter adapter = newAdapter();

    RecyclerView mRecyclerView = view.findViewById(R.id.messagesList);

    adapter.registerAdapterDataObserver(
        new RecyclerView.AdapterDataObserver() {
          @Override
          public void onItemRangeInserted(int positionStart, int itemCount) {
            mRecyclerView.smoothScrollToPosition(0);
          }
        });

    mRecyclerView.setAdapter(adapter);
  }

  private void onSendClick() {
    String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    String name = "User " + uid.substring(0, 6);

    EditText mMessageEdit = view.findViewById(R.id.messageEdit);
    onAddMessage(new ChatModel(name, mMessageEdit.getText().toString(), uid));
    mMessageEdit.setText("");
  }

  @NonNull
  private RecyclerView.Adapter newAdapter() {
    FirestoreRecyclerOptions<ChatModel> options =
        new FirestoreRecyclerOptions.Builder<ChatModel>()
            .setQuery(sChatQuery, ChatModel.class)
            .setLifecycleOwner(this)
            .build();

    return new FirestoreRecyclerAdapter<ChatModel, MessageView>(options) {
      @NonNull
      @Override
      public MessageView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MessageView(
            LayoutInflater.from(parent.getContext()).inflate(R.layout.message, parent, false));
      }

      @Override
      protected void onBindViewHolder(
              @NonNull MessageView holder, int position, @NonNull ChatModel model) {
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
            Objects.requireNonNull(getActivity()),
            e -> Log.e("ChatPage", "Failed to send message", e));
  }
}
