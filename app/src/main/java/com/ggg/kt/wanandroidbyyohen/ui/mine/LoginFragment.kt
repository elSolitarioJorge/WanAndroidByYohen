package com.ggg.kt.wanandroidbyyohen.ui.mine

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
import com.ggg.kt.wanandroidbyyohen.databinding.FragmentLoginBinding
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
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

        binding.btnLogin.setOnClickListener {
            viewModel.login(
                username = binding.etUsername.text.toString().trim(),
                password = binding.etPassword.text.toString().trim()
            )
        }

        binding.tvRegister.setOnClickListener {
            viewModel.register(
                username = binding.etUsername.text.toString().trim(),
                password = binding.etPassword.text.toString().trim()
            )
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loginState.collect { state ->
                    when (state) {
                        null -> Unit

                        is UiState.Loading -> {
                            binding.tvState.text = "处理中..."
                            binding.btnLogin.isEnabled = false
                        }

                        is UiState.Success -> {
                            binding.btnLogin.isEnabled = true
                            Toast.makeText(requireContext(), "登录成功", Toast.LENGTH_SHORT).show()
                            findNavController().popBackStack()
                        }

                        is UiState.Error -> {
                            binding.btnLogin.isEnabled = true
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
