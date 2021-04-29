package com.aqube.ram.ui.characterlist

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.aqube.ram.R
import com.aqube.ram.base.BaseFragment
import com.aqube.ram.databinding.FragmentCharacterListBinding
import com.aqube.ram.extension.observe
import com.aqube.ram.presentation.viewmodel.BaseViewModel
import com.aqube.ram.presentation.viewmodel.CharacterListViewModel
import com.aqube.ram.presentation.viewmodel.CharacterUIModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CharacterListFragment : BaseFragment<FragmentCharacterListBinding, BaseViewModel>() {

    override val layoutId: Int = R.layout.fragment_character_list

    override val viewModel: CharacterListViewModel by viewModels()

    @Inject
    lateinit var characterAdapter: CharacterAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val isFavorite =
            (findNavController().currentDestination?.label == getString(R.string.menu_favorites))
        viewModel.getCharacters(isFavorite)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe(viewModel.getCharacters(), ::onViewStateChange)
        initRecyclerView()
    }

    private fun initRecyclerView() {
        viewBinding.recyclerViewCharacters.apply {
            adapter = characterAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        characterAdapter.setItemClickListener { character ->
            findNavController().navigate(
                CharacterListFragmentDirections.actionCharacterListFragmentToCharacterDetailFragment(
                    character.id.toLong()
                )
            )
        }
    }

    private fun onViewStateChange(event: CharacterUIModel) {
        if (event.isRedelivered) return
        when (event) {
            is CharacterUIModel.Error -> handleErrorMessage(event.error)
            CharacterUIModel.Loading -> handleLoading(true)
            is CharacterUIModel.Success -> {
                handleLoading(false)
                event.data.let {
                    characterAdapter.list = it
                }
            }
        }
    }
}