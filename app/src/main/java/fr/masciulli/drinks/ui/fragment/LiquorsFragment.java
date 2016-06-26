package fr.masciulli.drinks.ui.fragment;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import fr.masciulli.drinks.R;
import fr.masciulli.drinks.model.Liquor;
import fr.masciulli.drinks.net.DataProvider;
import fr.masciulli.drinks.ui.activity.LiquorActivity;
import fr.masciulli.drinks.ui.adapter.ItemClickListener;
import fr.masciulli.drinks.ui.adapter.LiquorsAdapter;
import fr.masciulli.drinks.ui.adapter.holder.TileViewHolder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

public class LiquorsFragment extends Fragment implements Callback<List<Liquor>>,
        ItemClickListener<Liquor> {
    private static final String TAG = LiquorsFragment.class.getSimpleName();
    private static final String STATE_LIQUORS = "state_liquors";

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private View errorView;

    private DataProvider provider;
    private LiquorsAdapter adapter;
    private Call<List<Liquor>> call;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        provider = new DataProvider(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_liquors, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        errorView = rootView.findViewById(R.id.error);
        Button refreshButton = (Button) rootView.findViewById(R.id.refresh);

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadLiquors();
            }
        });

        int columnCount = getResources().getInteger(R.integer.column_count);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL));
        adapter = new LiquorsAdapter();
        adapter.setItemClickListener(this);
        recyclerView.setAdapter(adapter);

        if (savedInstanceState == null) {
            loadLiquors();
        } else {
            List<Liquor> liquors = savedInstanceState.getParcelableArrayList(STATE_LIQUORS);
            onLiquorsRetrieved(liquors);
        }

        return rootView;
    }

    private void loadLiquors() {
        displayLoadingState();
        cancelPreviousCall();
        call = provider.getLiquors();
        call.enqueue(this);
    }

    private void cancelPreviousCall() {
        if (call != null) {
            call.cancel();
        }
    }

    @Override
    public void onResponse(Call<List<Liquor>> call, Response<List<Liquor>> response) {
        if (response.isSuccessful()) {
            onLiquorsRetrieved(response.body());
        } else {
            displayErrorState();
            Log.e(TAG, "Couldn't retrieve liquors : " + response.message());
        }
    }

    private void onLiquorsRetrieved(List<Liquor> liquors) {
        adapter.setLiquors(liquors);
        displayNormalState();
    }

    @Override
    public void onFailure(Call<List<Liquor>> call, Throwable t) {
        Log.e(TAG, "Couldn't retrieve liquors", t);
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

    @Override
    public void onItemClick(int position, Liquor liquor) {
        Intent intent = new Intent(getActivity(), LiquorActivity.class);
        intent.putExtra(LiquorActivity.EXTRA_LIQUOR, liquor);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TileViewHolder holder = (TileViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
            String transition = getString(R.string.transition_liquor);
            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation(getActivity(), holder.getImageView(), transition);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STATE_LIQUORS, adapter.getLiquors());
    }
}
