package com.rsschool.quiz

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.rsschool.quiz.databinding.FragmentQuizBinding

class FragmentQuiz : Fragment() {
    private var binding: FragmentQuizBinding? = null // ViewBinding для текущего фрагмента
    private lateinit var fragmentQuizOnClick: FragmentQuizOnClick // интерфейс для отправки данных во второй фрагмент и замены

    // Пред-создание (реализация интерфейса)
    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentQuizOnClick =
            context as FragmentQuizOnClick // преобразовываем реализацию интерфейса
    }

    // ЛОГИКА
    // Вопросы, кол-во вопросв, ответы и правльные ответы
    private val questions = arrayOf("1 ", "2 ", "3 ", "4 ", "5 ") // вопросы
    private val cntQuestions = questions.size // кол-во вопросов
    private val answer = arrayOf( // ответы, сколько вопросов и Radio кнопок - столько же ответов
        arrayOf("1 ", "2 ", "3 ", "4 ", "5 "),
        arrayOf("1 ", "2 ", "3 ", "4 ", "5 "),
        arrayOf("1 ", "2 ", "3 ", "4 ", "5 "),
        arrayOf("1 ", "2 ", "3 ", "4 ", "5 "),
        arrayOf("1 ", "2 ", "3 ", "4 ", "5 ")

    )

    private var rightAnswer = arrayOf(0, 1, 2, 3, 4) // правильные ответы (начинаются с 0)
    /*
    Варианты места хранения данных:
    - извеняюсь за 3е лицо =) это не научная статья...
    ПРОБЛЕМА РЕШЕНА - через cntRunQuiz логику )

    1) В Activity
    Минус: Мы тратим на обработку данных (ответы)
    Плюс: Мы не постоянно генерируем объект с данными и у нас есть размер

    2) Во Fragment'е
    Минус: Мы постоянно генерируется объекты и у нас данные об кол-ве вопросов отдельно
    Плюс: Мы не тратим на обработку данных (ответов)

    Другие варианты:
    В Android ресурсах
    Минус: Придется менять логику и все равно будет храниться или там, или тут, непонятно как хранить массив в массиве
    Плюс: Можно локализовать (перевести), нету постоянно генерации (обработка под вопросом)

    Частичное. Хранить Вопросы и их кол-во в Activity, а ответы во Fragment'е
    Минус: Не централизованное хранение данных, придется и там и тут править
    Плюс: Мы не тратим на обработку данных, не все генерируем, не надо менять кол-во вопросов отдельно

    Возможно есть что-то ещё...
     */

    // Статичные данные
    private val titleQuestion = "Question " // текст сверху
    private val themeResource = arrayOf( // список тем
        R.style.Theme_Quiz_One,
        R.style.Theme_Quiz_Two,
        R.style.Theme_Quiz_Three,
        R.style.Theme_Quiz_Four,
        R.style.Theme_Quiz_Five
    )
    private val colorResource = arrayOf( // список цветов
        R.color.deep_orange_100_dark,
        R.color.yellow_100_dark,
        R.color.light_green_100_dark,
        R.color.cyan_100_dark,
        R.color.deep_purple_100_dark
    )

    // Хранимые данные
    private var index = 0 // текущий индекс (для визуализации + 1)

    // Данные результата и сохранения
    private var giveNumAnswers = IntArray(cntQuestions, { 0 }) // полученные ответы - их номера
    private var giveIdAnswers = IntArray(cntQuestions, { -1 }) // полученные ответы - их id

    // Создание визуализации
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Получаем хранимые данные (номер, номера данных ответов, ид данных Radio Button'ов ответы)
        index = arguments?.getInt(NamePublic.INDEX)!!

        if (NamePublic.cntRunQuiz >= 1) {
            giveNumAnswers = arguments?.getIntArray(NamePublic.GIVE_NUM_ANSWERS)!!
            giveIdAnswers = arguments?.getIntArray(NamePublic.GIVE_ID_ANSWERS)!!
        } else {
            NamePublic.cntRunQuiz++
        }

        // Установка темы из нашего массива
        activity?.setTheme(themeResource[index])
        // Установка статус бара
        activity?.window?.statusBarColor =
            ContextCompat.getColor(requireActivity(), colorResource[index])


        inflater.inflate(R.layout.fragment_quiz, container, false) // раздуваем View'ху
        binding = FragmentQuizBinding.inflate(inflater, container, false) // реализовываем binding
        return binding?.root // заполняем Fragment из binding (View'ой)
    }

    private var radioGroup: RadioGroup? = null
    private var nextButton: Button? = null
//    var toolbar: Toolbar? = null // import androidx.appcompat.widget.Toolbar


    // При созданном View. Обращение к элементам и их установкам
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        // НЕБОЛЬШАЯ ОПТИМИЗАЦИЯ ) Не все включенно, ради быстроты запуска (и минимума начальной обработки)
        radioGroup = binding?.radioGroup // было использованно 6 раз
        nextButton = binding?.nextButton // было использованно 5 раз
//        toolbar = binding?.toolbar // было использованно 4 раз

        // Обновление данных  и установка выбранных ответов
        updateData()
        updateCheckedAnswer()

        // Слушатель для кнопки Next
        nextButton?.setOnClickListener {
            onNext()
        }

        // Слушатель для кнопки Previous
        binding?.previousButton?.setOnClickListener {
            onPrevious()
        }

        // Слушатель для навигационной кнопки (<) ToolBar'ра
        binding?.toolbar?.setNavigationOnClickListener {
            onPrevious()
        }

    }

    // Установка данных и обновление интерфейса
    private fun updateData() {
        // Вставка номера квиза и вопрос из массива
        binding?.toolbar?.title = titleQuestion + (index + 1)
        binding?.question?.text = questions[index]

        // Вставка в RadioButton ответы из массива
        for (i in 0 until cntQuestions) {
            val radioButton =
                radioGroup?.getChildAt(i) as RadioButton // получаем Radio кнопку (дочку, это же ViewGroup)
            radioButton.text = answer[index][i] // подставляем ответ из массива
        }

        // При первом вопросе нету в тул-баре кнопки назад и не активна кнопка Previous
        if (index == 0) {
            binding?.toolbar?.navigationIcon = null // даем никакую иконку )
            binding?.previousButton?.isEnabled = false // кнопка Previous не активная
        }

        // Кода осталось до ответа один шаг "Next", когда мы уже в конце "Done"
        if (index == (cntQuestions - 2))
            nextButton?.text = getString(R.string.next)
        if (index == (cntQuestions - 1))
            nextButton?.text = getString(R.string.submit)

        // Проверка дан ли ответ. При не выбранном ответе будет кнопка не актиная (и на оборот)
        if (getNumAnswer() == -1)
            nextButton?.isEnabled = false // делаем кнопку Next не активной

        radioGroup?.setOnCheckedChangeListener { _, idRB -> // тут было: radioGroup, idRB
            if (getNumAnswer() != -1) // При данном ответе будет кнопка актиная
                nextButton?.isEnabled = true // делаем кнопку Next активной

        }


    }

    // Обновляем галочки (для выделенной ранее Radio-кнопки или очистка при отсутвии выбора)
    private fun updateCheckedAnswer() {
        val id = giveIdAnswers[index] // получаем из массива выборанную Radio кнопку, её id
        if (id != -1) // если у нас был сделан выбор (имеем id)
            radioGroup?.check(id) // устанавливаем галочку для выбранной Radio кнопки через id
        else
            radioGroup?.clearCheck() // очищаем недавно выбранный ответ
    }

    // Сохранение ответа (его номер и ID Radio кнопки)
    private fun saveAnswer() {
        giveNumAnswers[index] = getNumAnswer() // сохранение ответа (в виде индекса)
        giveIdAnswers[index] =
            radioGroup?.checkedRadioButtonId!! // сохранение ответа (в виде ид радио кнопок)
    }

    // Получение номера (после проверки его на id)
    private fun getNumAnswer(): Int {
        val id = radioGroup?.checkedRadioButtonId // получем id выбранной кнопки

        // Возращаем номер при определенном id, иначе -1 (для дальнейшей обработки и блокировании)
        return when (id) {
            R.id.option_one -> 0
            R.id.option_two -> 1
            R.id.option_three -> 2
            R.id.option_four -> 3
            R.id.option_five -> 4
            else -> -1
        }

    }

    // Обработка следующего вопроса
    private fun onNext() {
        if (getNumAnswer() != -1) { // если выбран хоть один ответ

            if (index == 0) { // если мы перешли от первого то возращаем кнопки < назад и делаем акнивной Previous
                binding?.toolbar?.setNavigationIcon(R.drawable.ic_baseline_chevron_left_24)
                binding?.previousButton?.isEnabled = true
            }

            saveAnswer() // сохраняем выбор
            if ((index + 1) < cntQuestions) { // что бы не превышало кол-во
                index++
                updateCheckedAnswer()
                updateData()

                fragmentQuizOnClick.onClickFragmentQuizNewQuestion(
                    index,
                    giveNumAnswers,
                    giveIdAnswers
                )
            } else { // открытие окончательного фрагмента

                val result: Int // результат в проценте
                var countRight = 0 // кол-во правильных ответов
                val namesDoneAnswers = Array(cntQuestions, { "" }) // имена
                for (i in 0 until cntQuestions) {
                    if (giveNumAnswers[i] == rightAnswer[i])
                        countRight++
                    namesDoneAnswers[i] = answer[i][giveNumAnswers[i]]
                }

                // Расчет процента правильных ответов
                result = (countRight * 100) / cntQuestions

                // Открытие результата
                fragmentQuizOnClick.onClickFragmentQuizToResult(
                    result,
                    questions,
                    namesDoneAnswers
                )
            }

        }
    }

    // Обработка предыдущего вопроса
    private fun onPrevious() {
        if (index > 0) {
            saveAnswer()
            index--
            updateData()
            updateCheckedAnswer()
            fragmentQuizOnClick.onClickFragmentQuizNewQuestion(index, giveNumAnswers, giveIdAnswers)
        }
    }

    // При удалении визуализации
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null // чистим View Binding
    }

}