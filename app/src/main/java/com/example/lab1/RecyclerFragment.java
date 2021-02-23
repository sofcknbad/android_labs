package com.example.lab1;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerFragment extends Fragment {

	RecyclerAdapter recyclerAdapter;
	RecyclerView recyclerView;
	static int backButton = 0;

	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);
		recyclerAdapter = new RecyclerAdapter();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_elements, container, false);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		OnBackPressedCallback callback = new OnBackPressedCallback(true) {
			@Override
			public void handleOnBackPressed() {
				switch (backButton) {
					case 1:
						backButton--;
					case 0:
						Toast.makeText(getActivity(), "Еще раз для выхода", Toast.LENGTH_SHORT).show();
						backButton--;
						new CountDownTimer(2000, 1000) {
							public void onTick(long millisUntilFinished) { }
							public void onFinish() { backButton = 0; }}.start();
					break;
					case -1:

						getActivity().moveTaskToBack(true);
						break;
					default:

						break;
				}
			}
		};
		requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);


		recyclerView = getActivity().findViewById(R.id.recycler);
		recyclerView.setLayoutManager(new LinearLayoutManager(null, LinearLayoutManager.VERTICAL,
				false));
		recyclerView.setHasFixedSize(true);
		recyclerView.setAdapter(recyclerAdapter);

		ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
				ItemTouchHelper.LEFT) {

			@Override
			public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
								  @NonNull RecyclerView.ViewHolder viewHolder1) {
				return false;
			}

			@Override
			public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
				recyclerAdapter.removeItemAt(viewHolder.getAdapterPosition());
			}
		});
		itemTouchHelper.attachToRecyclerView(recyclerView);

		recyclerAdapter.setCountOfElement(Main.mediaArray.size());
	}
}