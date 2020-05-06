package ch.epfl.favo.view.tabs.shop;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

import ch.epfl.favo.R;
import ch.epfl.favo.util.CommonTools;

public class ShopPage extends Fragment {

  private static final List<ShopItem> ITEMS =
      Arrays.asList(
          new ShopItem(R.drawable.favo_coin, 10, 1),
          new ShopItem(R.drawable.favo_coin_two, 50, 3),
          new ShopItem(R.drawable.favo_coin_three, 100, 5),
          new ShopItem(R.drawable.favo_coin_six, 1000, 10),
          new ShopItem(R.drawable.favo_coin_ten, 5000, 30),
          new ShopItem(R.drawable.favo_coin_multiple, 10000, 50));

  private static final String ARG_COLUMN_COUNT = "column-count";
  private int mColumnCount = 2;

  public ShopPage() {}

  // TODO: Customize parameter initialization
  @SuppressWarnings("unused")
  public static ShopPage newInstance(int columnCount) {
    ShopPage fragment = new ShopPage();
    Bundle args = new Bundle();
    args.putInt(ARG_COLUMN_COUNT, columnCount);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (getArguments() != null) {
      mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
    }
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_shop, container, false);

    TextView currentBalance = view.findViewById(R.id.current_balance_text);

    // temporary, should get balance from database
    currentBalance.setText(getString(R.string.balance_text, 200));

    RecyclerView recyclerView = view.findViewById(R.id.shop_items_list);

    if (mColumnCount <= 1) {
      recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    } else {
      recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), mColumnCount));
    }

    recyclerView.setAdapter(getRecyclerViewAdapter());

    return view;
  }

  private RecyclerView.Adapter getRecyclerViewAdapter() {
    return new RecyclerView.Adapter() {
      @NonNull
      @Override
      public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =
            LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_shop_item, parent, false);

        view.setOnClickListener(
            v -> {
              // do something, temporary for now
              CommonTools.showSnackbar(getView(), getString(R.string.shop_pay_message));
            });

        return new ShopItemViewHolder(view);
      }

      @Override
      public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ShopItemViewHolder shopHolder = ((ShopItemViewHolder) holder);
        shopHolder.imageView.setImageResource(ITEMS.get(position).resourceId);
        shopHolder.quantityView.setText(String.valueOf(ITEMS.get(position).quantity));
        shopHolder.priceView.setText(getString(R.string.price_text, ITEMS.get(position).price));
      }

      @Override
      public int getItemCount() {
        return ITEMS.size();
      }
    };
  }
}
