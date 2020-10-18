package com.laioffer.tinnews.ui.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.laioffer.tinnews.R;
import com.laioffer.tinnews.databinding.FragmentHomeBinding;
import com.laioffer.tinnews.model.Article;
import com.laioffer.tinnews.repository.NewsRepository;
import com.laioffer.tinnews.repository.NewsViewModelFactory;
import com.laioffer.tinnews.ui.save.SaveFragment;
import com.laioffer.tinnews.ui.save.SaveFragmentDirections;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.Duration;
import com.yuyakaido.android.cardstackview.RewindAnimationSetting;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting;

import java.util.List;


public class HomeFragment extends Fragment implements CardStackListener{
    private HomeViewModel viewModel;
    private FragmentHomeBinding binding;
    private CardStackLayoutManager layoutManager;
    private List<Article> articles;



    public HomeFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // setup CardStackView
        CardSwipeAdapter swipeAdapter = new CardSwipeAdapter();
        layoutManager = new CardStackLayoutManager(requireContext(), this);
        layoutManager.setStackFrom(StackFrom.Top);
        binding.homeCardStackView.setLayoutManager(layoutManager);
        binding.homeCardStackView.setAdapter(swipeAdapter);

        // Handle like unlike button clicks
        binding.homeLikeButton.setOnClickListener(v -> HomeFragment.this.swipeCard(Direction.Right));
        binding.homeUnlikeButton.setOnClickListener(v -> HomeFragment.this.swipeCard(Direction.Left));
        binding.restoreBackButton.setOnClickListener(v -> HomeFragment.this.rollBack(Direction.Bottom));
        // Handle check details




        NewsRepository repository = new NewsRepository(getContext());
        viewModel = new ViewModelProvider(this, new NewsViewModelFactory(repository)).get(HomeViewModel.class);
        viewModel.setCountryInput("us");
        viewModel.getTopHeadlines().observe(getViewLifecycleOwner(), newsResponse -> {
            if (newsResponse != null) {
                articles = newsResponse.articles;
                swipeAdapter.setArticles(articles);
            }
        });

        swipeAdapter.setItemCallback(new CardSwipeAdapter.ItemCallback() {
            @Override
            public void onOpenDetails(Article article) {
                Log.d("hehe", article.toString());
                HomeFragmentDirections.ActionNavigationHomeToNavigationDetails
                        direction = HomeFragmentDirections.actionNavigationHomeToNavigationDetails(article);
                NavHostFragment.findNavController(HomeFragment.this).navigate(direction);
            }
        });

    }


    @Override
    public void onCardDragging(Direction direction, float ratio) {

    }

    @Override
    public void onCardSwiped(Direction direction) {
        if (direction == Direction.Left) {
            Log.d("CardStackView", "Unliked " + layoutManager.getTopPosition());
            Toast.makeText(HomeFragment.this.getContext(), "DisLike this news", Toast.LENGTH_SHORT).show();
        } else if (direction == Direction.Right) {
            Log.d("CardStackView", "Liked "  + layoutManager.getTopPosition());
            Article article = articles.get(layoutManager.getTopPosition() -1);
            viewModel.setFavoriteArticleInput(article).observe(getViewLifecycleOwner(), success ->
                    Toast.makeText(HomeFragment.this.getContext(),
                    success ? "Successfully Save!" : "Already Save Before", Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    public void onCardRewound() {
        Toast.makeText(HomeFragment.this.getContext(), "Check Previous News", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCardCanceled() {

    }

    @Override
    public void onCardAppeared(View view, int position) {

    }

    @Override
    public void onCardDisappeared(View view, int position) {

    }

    private void swipeCard(Direction direction) {
        SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
                .setDirection(direction)
                .setDuration(Duration.Normal.duration)
                .build();
        layoutManager.setSwipeAnimationSetting(setting);
        binding.homeCardStackView.swipe();
    }
    private void rollBack(Direction direction){
        RewindAnimationSetting setting = new RewindAnimationSetting.Builder()
                .setDirection(direction)
                .setDuration(Duration.Normal.duration)
                .build();
        layoutManager.setRewindAnimationSetting(setting);
        binding.homeCardStackView.rewind();
    }
}

