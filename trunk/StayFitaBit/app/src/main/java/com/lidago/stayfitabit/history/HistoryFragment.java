package com.lidago.stayfitabit.history;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


import com.firebase.client.Firebase;
import com.lidago.stayfitabit.Args;
import com.lidago.stayfitabit.R;
import com.lidago.stayfitabit.firebase.FirebaseClient;

/**
 * Created on 01.06.2016.
 */
public class HistoryFragment extends Fragment {

    private RecyclerView mHistoryRecyclerView;
    private HistoryViewAdapter mAdapter;

    public static HistoryFragment newInstance(String title) {
        Bundle args = new Bundle();
        args.putString(Args.TOOLBAR_TITLE, title);
        HistoryFragment fragment = new HistoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle bundle) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Firebase.setAndroidContext(getActivity());
        init();
    }

    private void init() {
        setupUserInterface();
        setupToolbar();
    }

    private void setupUserInterface() {
        mHistoryRecyclerView = (RecyclerView) getView().findViewById(R.id.history_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mHistoryRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new HistoryViewAdapter(getActivity(), FirebaseClient.getInstance().getAllItems(), new HistoryViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(HistoryModel item, int position) {
            }
        });
        mHistoryRecyclerView.setAdapter(mAdapter);
        FirebaseClient.getInstance().setAdapter(mAdapter);
    }

    private void setupToolbar() {
        String title = getArguments().getString(Args.TOOLBAR_TITLE);
        // We have a Toolbar in place so we don't need to care about the NPE warning
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_history_toolbar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.history_all:
                mAdapter.setHistoryItems(FirebaseClient.getInstance().getAllItems());
                mAdapter.notifyDataSetChanged();
                item.setChecked(true);
                break;

            case R.id.history_run:
                mAdapter.setHistoryItems(FirebaseClient.getInstance().getRunItems());
                mAdapter.notifyDataSetChanged();
                item.setChecked(true);
                break;

            case R.id.history_pushups:
                mAdapter.setHistoryItems(FirebaseClient.getInstance().getPushUpItems());
                mAdapter.notifyDataSetChanged();
                item.setChecked(true);
                break;

            case R.id.history_latest:
                FirebaseClient.getInstance().sortLists();
                mAdapter.notifyDataSetChanged();
                item.setChecked(true);
                break;
            case R.id.history_oldest:
                FirebaseClient.getInstance().sortListsReverse();
                mAdapter.notifyDataSetChanged();
                item.setChecked(true);
                break;
        }
        return true;
    }
}
