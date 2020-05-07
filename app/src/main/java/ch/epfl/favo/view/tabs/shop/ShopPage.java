package ch.epfl.favo.view.tabs.shop;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import ch.epfl.favo.R;
import ch.epfl.favo.util.CommonTools;

public class ShopPage extends Fragment {

  private static final List<ShopItem> COINS =
      Arrays.asList(
          new ShopItem(R.drawable.favo_coin, 10, 0.99, null),
          new ShopItem(R.drawable.favo_coin_two, 50, 2.99, null),
          new ShopItem(R.drawable.favo_coin_three, 100, 4.99, null),
          new ShopItem(R.drawable.favo_coin_six, 1000, 9.99, null),
          new ShopItem(R.drawable.favo_coin_ten, 5000, 29.99, null),
          new ShopItem(R.drawable.favo_coin_multiple, 10000, 49.99, null));
  private static final List<ShopItem> OFFERS =
      Arrays.asList(
          new ShopItem(R.drawable.favo_coin_three, 30, 1.49, new Date()),
          new ShopItem(R.drawable.favo_coin_six, 500, 6.99, new Date()),
          new ShopItem(R.drawable.favo_coin_ten, 1500, 12.99, new Date()),
          new ShopItem(R.drawable.favo_coin_multiple, 500000, 99.99, new Date()));
  private static final String ARG_COLUMN_COUNT = "column-count";
  private List<ShopItem> shopItems;
  private RecyclerView.Adapter adapter;
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

    shopItems = OFFERS;

    //    TextView currentBalance = view.findViewById(R.id.current_balance_text);
    //
    //    // temporary, should get balance from database
    //    currentBalance.setText(String.valueOf(200));

    RecyclerView recyclerView = view.findViewById(R.id.shop_items_list);

    if (mColumnCount <= 1) {
      recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    } else {
      recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), mColumnCount));
    }

    adapter = getRecyclerViewAdapter();
    recyclerView.setAdapter(adapter);

    setupToolBar();

    setupTabLayout(view);

    return view;
  }

  private void setupTabLayout(View view) {
    ((TabLayout) view.findViewById(R.id.tab_layout))
        .addOnTabSelectedListener(
            new TabLayout.OnTabSelectedListener() {
              @Override
              public void onTabSelected(TabLayout.Tab tab) {

                if (Objects.equals(tab.getText(), getText(R.string.buy_favo_coins_text))) {
                  shopItems = COINS;
                  adapter.notifyDataSetChanged();
                }

                if (Objects.equals(tab.getText(), getText(R.string.special_offers_text))) {
                  shopItems = OFFERS;
                  adapter.notifyDataSetChanged();
                }
              }

              @Override
              public void onTabUnselected(TabLayout.Tab tab) {}

              @Override
              public void onTabReselected(TabLayout.Tab tab) {}
            });
  }

  private void setupToolBar() {
    Toolbar toolbar = requireActivity().findViewById(R.id.toolbar_main_activity);
    toolbar.setBackgroundColor(getResources().getColor(R.color.material_green_500));
    toolbar.setTitleTextColor(Color.WHITE);
    toolbar.setTitle(R.string.shop);
    Objects.requireNonNull(toolbar.getNavigationIcon())
        .setColorFilter(new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP));
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
        shopHolder.imageView.setImageResource(shopItems.get(position).resourceId);
        shopHolder.quantityView.setText(String.valueOf(shopItems.get(position).quantity));
        shopHolder.priceView.setText(getString(R.string.price_text, shopItems.get(position).price));

        if (shopItems.get(position).expiration != null) {
          shopHolder.expirationView.setVisibility(View.VISIBLE);
          SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy", Locale.US);
          shopHolder.expirationView.setText(dateFormat.format(shopItems.get(position).expiration));
        } else {
          shopHolder.expirationView.setVisibility(View.GONE);
        }
      }

      @Override
      public int getItemCount() {
        return shopItems.size();
      }
    };
  }
}
