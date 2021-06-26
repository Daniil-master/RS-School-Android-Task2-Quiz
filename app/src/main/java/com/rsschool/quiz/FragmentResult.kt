package com.rsschool.quiz

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.rsschool.quiz.databinding.FragmentResultBinding

class FragmentResult : Fragment() {
    private var binding: FragmentResultBinding? = null // ViewBinding для текущего фрагмента
    private lateinit var fragmentResultOnClick: FragmentResultOnClick // интерфейс для отправки данных во второй фрагмент и замены

    // Пред-создание
    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentResultOnClick = context as FragmentResultOnClick // преобразовываем реализацию
    }

    // Создание визуализации
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        inflater.inflate(R.layout.fragment_result, container, false) // раздуваем View'ху
        binding = FragmentResultBinding.inflate(inflater, container, false) // реализовываем binding

        return binding?.root //заполняем Fragment из binding (View'ой)

    }

    // ЛОГИКА
    private val titleResult = "Your result:"

    // При созданном View. Обращение к элементам и их установкам
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        val result = arguments?.getInt(NamePublic.RESULT)
        binding?.txtResult?.text = "$titleResult $result%"

        // Слушатель для картинки Close
        binding?.imgClose?.setOnClickListener {
            fragmentResultOnClick.onClickFragmentResultBack(true) // отправляем указание закрыть (вызвать onBack)
        }

        // Слушатель для картинки Restart
        binding?.imgRestart?.setOnClickListener {
            fragmentResultOnClick.onClickFragmentResultBack(false) // отправляем указание пересоздать (не закрывать)
        }

        // Слушатель для картинки Share
        binding?.imgShare?.setOnClickListener {
            fragmentResultOnClick.onClickFragmentResultSendEmail() // формируем отправку на почту
        }
    }

    // При удалении визуализации
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null // чистим View Binding
    }

}