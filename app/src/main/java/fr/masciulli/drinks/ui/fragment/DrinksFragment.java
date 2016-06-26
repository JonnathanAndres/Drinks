package fr.masciulli.drinks.ui.fragment;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import fr.masciulli.drinks.R;
import fr.masciulli.drinks.model.Drink;
import fr.masciulli.drinks.net.DataProvider;
import fr.masciulli.drinks.ui.activity.DrinkActivity;
import fr.masciulli.drinks.ui.adapter.DrinksAdapter;
import fr.masciulli.drinks.ui.adapter.ItemClickListener;
import fr.masciulli.drinks.ui.adapter.holder.TileViewHolder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

public class DrinksFragment extends Fragment implements Callback<List<Drink>>, SearchView.OnQueryTextListener, ItemClickListener<Drink> {
    private static final String TAG = DrinksFragment.class.getSimpleName();

    private static final String STATE_DRINKS = "state_drinks";

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private View emptyView;
    private View errorView;

    private DataProvider provider;
    private DrinksAdapter adapter;
    private Call<List<Drink>> call;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        provider = new DataProvider(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View rootView = inflater.inflate(R.layout.fragment_drinks, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        emptyView = rootView.findViewById(R.id.empty);
        errorView = rootView.findViewById(R.id.error);
        Button refreshButton = (Button) rootView.findViewById(R.id.refresh);

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDrinks();
            }
        });

        int columnCount = getResources().getInteger(R.integer.column_count);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL));
        adapter = new DrinksAdapter();
        adapter.setItemClickListener(this);
        recyclerView.setAdapter(adapter);

        if (savedInstanceState == null) {
            loadDrinks();
        } else {
            List<Drink> drinks = savedInstanceState.getParcelableArrayList(STATE_DRINKS);
            onDrinksRetrieved(drinks);
        }

        return rootView;
    }

    private void loadDrinks() {
        displayLoadingState();
        cancelPreviousCall();
        call = provider.getDrinks();
        call.enqueue(this);
    }

    private void cancelPreviousCall() {
        if (call != null) {
            call.cancel();
        }
    }

    @Override
    public void onResponse(Call<List<Drink>> call, Response<List<Drink>> response) {
        if (response.isSuccessful()) {
            onDrinksRetrieved(response.body());
        } else {
            displayErrorState();
            Log.e(TAG, "Couldn't retrieve drinks : " + response.message());
        }
    }

    private void onDrinksRetrieved(List<Drink> drinks) {
        adapter.setDrinks(drinks);
        displayNormalState();
    }

    @Override
    public void onFailure(Call<List<Drink>> call, Throwable t) {
        Log.e(TAG, "Couldn't retrieve drinks", t);
        displayErrorState();
    }

    private void displayLoadingState() {
        errorView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    private void displayErrorState() {
        errorView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
    }

    private void displayNormalState() {
        errorView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onItemClick(int position, Drink drink) {
        Intent intent = new Intent(getActivity(), DrinkActivity.class);
        intent.putExtra(DrinkActivity.EXTRA_DRINK, drink);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TileViewHolder holder = (TileViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
            String transition = getString(R.string.transition_drink);
            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation(getActivity(), holder.getImageView(), transition);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_drinks, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.filter(newText);
        if (adapter.getItemCount() == 0) {
            showEmptyView();
        } else {
            hideEmptyView();
        }
        return false;
    }

    private void showEmptyView() {
        emptyView.setVisibility(View.VISIBLE);
    }

    private void hideEmptyView() {
        emptyView.setVisibility(View.GONE);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // no-op
        return false;
    }

    @Override
    public void onDestroyOptionsMenu() {
        adapter.clearFilter();
        hideEmptyView();
        super.onDestroyOptionsMenu();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STATE_DRINKS, adapter.getDrinks());
    }

    @Override
    public void onStop() {
        cancelPreviousCall();
        super.onStop();
    }
}
