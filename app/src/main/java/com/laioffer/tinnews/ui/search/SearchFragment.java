package com.laioffer.tinnews.ui.search;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.laioffer.tinnews.R;
import com.laioffer.tinnews.databinding.FragmentSearchBinding;
import com.laioffer.tinnews.databinding.SearchNewsItemBinding;
import com.laioffer.tinnews.model.Article;
import com.laioffer.tinnews.repository.NewsRepository;
import com.laioffer.tinnews.repository.NewsViewModelFactory;
import com.laioffer.tinnews.ui.home.HomeFragment;
import com.yuyakaido.android.cardstackview.Direction;

import java.util.List;


public class SearchFragment extends Fragment {
    private SearchViewModel viewModel;
    private FragmentSearchBinding binding;
    private SearchNewsItemBinding itemBinding;
    private List<Article> articles;

    public SearchFragment() {
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
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SearchNewsAdapter newsAdapter = new SearchNewsAdapter();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 2);

        binding.newsResultsRecyclerView.setLayoutManager(gridLayoutManager);
        binding.newsResultsRecyclerView.setAdapter(newsAdapter);

//        itemBinding.searchItemFavorite.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Article article = articles.get(gridLayoutManager);
//                SearchFragment.this.;
//            }
//        });


        binding.newsSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()) {
                    viewModel.setSearchInput(query);
                }
                binding.newsSearchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        NewsRepository repository = new NewsRepository(getContext());
        viewModel = new ViewModelProvider(this, new NewsViewModelFactory(repository)).get(SearchViewModel.class);

        viewModel.searchNews().observe(getViewLifecycleOwner(), newsResponse -> {
            if (newsResponse != null) {
                Log.d("SearchFragment", newsResponse.toString());
                articles = newsResponse.articles;
                newsAdapter.setArticles(newsResponse.articles);
            }
        });

        newsAdapter.setItemCallback(new SearchNewsAdapter.ItemCallback() {
            @Override
            public void onOpenDetails(Article article) {
                SearchFragmentDirections.ActionNavigationSearchToNavigationDetails
                        direction = SearchFragmentDirections.actionNavigationSearchToNavigationDetails(article);
                NavHostFragment.findNavController(SearchFragment.this).navigate(direction);
            }

            @Override
            public void onSavedFavorite(Article article) {
                viewModel.setFavoriteArticleInput(article).observe(getViewLifecycleOwner(), success ->
                        Toast.makeText(SearchFragment.this.getContext(),
                                success ? "Successfully Save!" : "Already Save Before", Toast.LENGTH_SHORT).show());
            }
        });

    }

}