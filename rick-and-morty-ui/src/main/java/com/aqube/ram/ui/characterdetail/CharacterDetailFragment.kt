package com.aqube.ram.ui.characterdetail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.aqube.ram.R
import com.aqube.ram.base.BaseFragment
import com.aqube.ram.databinding.FragmentCharacterDetailBinding
import com.aqube.ram.extension.observe
import com.aqube.ram.extension.showSnackBar
import com.aqube.ram.presentation.viewmodel.BaseViewModel
import com.aqube.ram.presentation.viewmodel.Bookmark
import com.aqube.ram.presentation.viewmodel.CharacterDetailUIModel
import com.aqube.ram.presentation.viewmodel.CharacterDetailViewModel
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CharacterDetailFragment : BaseFragment<FragmentCharacterDetailBinding, BaseViewModel>() {

    override val layoutId: Int = R.layout.fragment_character_detail

    override val viewModel: CharacterDetailViewModel by viewModels()

    private val args: CharacterDetailFragmentArgs by navArgs()

    @Inject
    lateinit var glide: RequestManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe(viewModel.getCharacter(), ::onViewStateChange)
        viewModel.getCharacterDetail(args.characterId)
        setUiChangeListeners()
    }

    private fun setUiChangeListeners() {
        viewBinding.checkBoxBookmark.setOnCheckedChangeListener { view, isChecked ->
            if (!viewBinding.checkBoxBookmark.isPressed) {
                return@setOnCheckedChangeListener
            }
            if (isChecked)
                viewModel.setBookmarkCharacter(view.tag.toString().toLong())
            else
                viewModel.setUnBookmarkCharacter(view.tag.toString().toLong())
        }
    }

    private fun onViewStateChange(result: CharacterDetailUIModel) {
        if (result.isRedelivered) return
        when (result) {
            is CharacterDetailUIModel.BookMarkStatus -> {
                when (result.bookmark) {
                    Bookmark.BOOKMARK ->
                        if (result.status) {
                            showSnackBar(viewBinding.rootView, getString(R.string.bookmark_success))
                        } else {
                            handleErrorMessage(getString(R.string.bookmark_error))
                        }
                    Bookmark.UN_BOOKMARK ->
                        if (result.status) {
                            showSnackBar(
                                viewBinding.rootView,
                                getString(R.string.un_bookmark_success)
                            )
                        } else {
                            handleErrorMessage(getString(R.string.bookmark_error))
                        }
                }
            }
            is CharacterDetailUIModel.Error -> handleErrorMessage(result.error)
            CharacterDetailUIModel.Loading -> handleLoading(true)
            is CharacterDetailUIModel.Success -> {
                handleLoading(false)
                result.data.let { character ->
                    viewBinding.apply {
                        viewBinding.textViewCharacterName.text = character.name
                        glide.load(character.image).into(imageViewCharacter)
                        viewBinding.checkBoxBookmark.tag = character.id
                        viewBinding.checkBoxBookmark.isChecked = character.isBookMarked
                    }
                }
            }
        }
    }
}