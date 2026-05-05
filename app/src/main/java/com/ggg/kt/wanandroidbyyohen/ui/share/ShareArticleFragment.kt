package com.ggg.kt.wanandroidbyyohen.ui.share

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.ggg.kt.wanandroidbyyohen.common.base.UiState
import com.ggg.kt.wanandroidbyyohen.databinding.FragmentShareArticleBinding
import kotlinx.coroutines.launch

class ShareArticleFragment : Fragment() {

    private var _binding: FragmentShareArticleBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ShareArticleViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShareArticleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initClick()
        observeData()
    }

    private fun initClick() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnShare.setOnClickListener {
            viewModel.shareArticle(
                title = binding.etArticleTitle.text.toString(),
                link = binding.etArticleLink.text.toString()
            )
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.shareState.collect { state ->
                    when (state) {
                        null -> Unit

                        is UiState.Loading -> {
                            binding.btnShare.isEnabled = false
                            binding.btnShare.text = "正在提交..."
                            binding.tvState.text = ""
                        }

                        is UiState.Success -> {
                            binding.btnShare.isEnabled = true
                            binding.btnShare.text = "提交分享"
                            Toast.makeText(
                                requireContext(),
                                "分享成功",
                                Toast.LENGTH_SHORT
                            ).show()
                            findNavController().popBackStack()
                        }

                        is UiState.Error -> {
                            binding.btnShare.isEnabled = true
                            binding.btnShare.text = "提交分享"
                            binding.tvState.text = state.message
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}