package com.example.greendoc

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.greendoc.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Click listeners for navigation
//        binding.manageSubscriptionCard.setOnClickListener {
//            startActivity(Intent(requireContext(), ManageSubscriptionActivity::class.java))
//        }
//
//        binding.contactUsCard.setOnClickListener {
//            startActivity(Intent(requireContext(), ContactUsActivity::class.java))
//        }
//
//        binding.shareAppCard.setOnClickListener {
//            startActivity(Intent(requireContext(), ShareAppActivity::class.java))
//        }
//
//        binding.privacyPolicyCard.setOnClickListener {
//            startActivity(Intent(requireContext(), PrivacyPolicyActivity::class.java))
//        }
//
//        binding.termsOfServiceCard.setOnClickListener {
//            startActivity(Intent(requireContext(), TermsOfServiceActivity::class.java))
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Prevent memory leaks
    }
}
