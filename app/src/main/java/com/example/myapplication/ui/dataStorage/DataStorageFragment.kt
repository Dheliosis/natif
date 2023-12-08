package com.example.myapplication.ui.dataStorage

import androidx.fragment.app.Fragment
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myapplication.databinding.FragmentDataStorageBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DataStorageFragment: Fragment() {
    private var _binding: FragmentDataStorageBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDataStorageBinding.inflate(inflater, container, false)

        sharedPreferences = requireActivity().getSharedPreferences("MySharedPrefs", Context.MODE_PRIVATE)

        loadData()

        binding.saveButton.setOnClickListener {
            saveData()
        }

        return binding.root
    }

    private fun saveData() {
        val stringData = binding.editTextString.text.toString()
        val intData = binding.editTextInt.text.toString().toIntOrNull() ?: 0
        val listData = binding.editTextList.text.toString().split(",").map { it.trim() }

        val editor = sharedPreferences.edit()
        editor.putString("myString", stringData)
        editor.putInt("myInt", intData)

        val gson = Gson()
        val json = gson.toJson(listData)
        editor.putString("myList", json)

        editor.apply()

        loadData()
    }

    private fun loadData() {
        // Récupérer les données
        val myString = sharedPreferences.getString("myString", "DefaultString")
        val myInt = sharedPreferences.getInt("myInt", 0)

        // Récupérer la liste
        val gson = Gson()
        val json = sharedPreferences.getString("myList", "")
        val type = object : TypeToken<List<String>>() {}.type
        val myList: List<String> = gson.fromJson(json, type) ?: listOf()

        // Mettre à jour l'interface utilisateur avec les données récupérées
        binding.textView.text = "String: $myString\nInt: $myInt\nList: $myList"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}