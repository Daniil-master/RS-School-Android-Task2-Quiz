package com.rsschool.quiz

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity(), FragmentQuizOnClick, FragmentResultOnClick {
    var fragmentResult: FragmentResult? =
        null // для доступности и получения состояние активности (работы)
    var isClose = false // состояние, указание от Результата


    // ЛОГИКА
    // Хранимые данные
    private var index = 0 // текущий индекс (для визуализации + 1)
    private var resultQuiz = 0 // имя result зарезервированная
    private var questionsQuiz = Array(0, { " " })
    private var namesAnswersQuiz = Array(0, { " " })

    // Данные результата и сохранения
    private var giveNumAnswers =
        IntArray(0) // реализованно авто-размере и заполнение через cntRunQuiz
    private var giveIdAnswers = IntArray(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        openFragmentQuiz() // открываем первый фрагмент
    }

    // Первый Фрагмент. Викторина
    private fun openFragmentQuiz() {
        val fragmentQuiz: Fragment = FragmentQuiz()
        val transaction =
            supportFragmentManager.beginTransaction() // создаем обычную транзакцию (для управления)

        // Передаем хранимые данные
        val bundle = Bundle()
        bundle.putInt(NamePublic.INDEX, index)
        bundle.putIntArray(NamePublic.GIVE_NUM_ANSWERS, giveNumAnswers)
        bundle.putIntArray(NamePublic.GIVE_ID_ANSWERS, giveIdAnswers)
        fragmentQuiz.arguments = bundle

        if (NamePublic.cntRunQuiz == -1) // для проверки запуска Quiz
            NamePublic.cntRunQuiz++

        transaction.replace(R.id.container, fragmentQuiz) // заменяем имеющийся на Quiz
        transaction.commit() // подтверждаем изменения (замену)
    }

    // Второй Фрагмент. Результат
    private fun openFragmentResult() {
        fragmentResult = FragmentResult()
        val transaction =
            supportFragmentManager.beginTransaction() // создаем обычную транзакцию (для управления)

        // Передаем для него результат (также можно пихать объекты Serializable и Parcelable =) )
        val bundle = Bundle()
        bundle.putInt(NamePublic.RESULT, resultQuiz)
        fragmentResult?.arguments = bundle

        transaction.replace(R.id.container, fragmentResult!!) // заменяем имеющийся на Результат
        transaction.commit() // подтверждаем изменения (замену)
    }

    // Создание нового вопроса (следующего или предыдущего), это при нажатии Next или Previous
    override fun onClickFragmentQuizNewQuestion(
        index: Int,
        giveNumAnswers: IntArray,
        giveIdAnswers: IntArray
    ) {
        // Получаем измененные данные (номер, номера выбранных ответов, ID'и Radio Button'ов из ответа)
        this.index = index
        this.giveNumAnswers = giveNumAnswers
        this.giveIdAnswers = giveIdAnswers
        openFragmentQuiz() // открываем Викторину
    }


    // Получем указание об закрытии или начать заново играть
    override fun onClickFragmentResultBack(isClose: Boolean) {
        this.isClose = isClose // получаем статус указания
        onBackPressed() // действуем через метод для кнопки Закрыть
    }

    // Слушатель кнопки Назад (на навигационном баре)
    override fun onBackPressed() {
        if (fragmentResult?.isResumed == true) { // проверяем что Fragment Result активен (работает)
            if (isClose) // при получении указания закрыть, вызываем наследуемую функцию
            {
                finish()
                exitProcess(0)
            }
//                finishAffinity()
            else { // при получении указание не закрывать
                // при нажатие Заново - обнуление результатов и заново открываем викторину
                NamePublic.cntRunQuiz = -1
                index = 0
                giveNumAnswers = IntArray(0)
                giveIdAnswers = IntArray(0)
                openFragmentQuiz()
            }
        }
    }

    // Получаем и формируем результат викторины, и его отображаем
    override fun onClickFragmentQuizToResult(
        result: Int,
        questions: Array<String>,
        namesDoneAnswers: Array<String>
    ) {
        /*
        Варианты формирования для почты результата:
        - извеняюсь за 3е лицо =) это не научная статья...
        ПРОБЛЕМА РЕШЕНА - выбран 2й вариант

         1) Сформировать тут обработку и сохранить строку
         Минус: Мы тратим на |обработку| при нажатии и |минимум ОЗУ| - Лишнее выполнение
         2) Сформировать при клике отправки сообщения
         Минус: Мы тратим на |ОЗУ (храня данные)| и при нажатии потом на |обработку| - Последующее выполнение

         Ранее была мини обработка на получение Имен ответов по индексу (сокращение хранимых данных)
         Опробован 1й способ, все таки 2й будет более рационален, т.к. лишнего действия не будет, ОЗУ задействована )
         */

        resultQuiz = result
        questionsQuiz = questions
        namesAnswersQuiz = namesDoneAnswers

        openFragmentResult() // отображаем Результат
    }

    // Посылаем в приложение почты сформированный результат
    override fun onClickFragmentResultSendEmail() {
        // Формируем результат
        var textMessage = "Your result: $resultQuiz% \n"
        for (i in questionsQuiz.indices) {
            textMessage += "${(i + 1)}) " + questionsQuiz[i] + "\n"
            textMessage += namesAnswersQuiz[i] + "\n\n"
        }


        val intent = Intent(Intent.ACTION_SENDTO) // активность отправка к источнику
        intent.data = Uri.parse("mailto:") // только для почтовой отправки

        intent.putExtra(Intent.EXTRA_SUBJECT, "Quiz Result") // название/титул/субъект сообщения
        intent.putExtra(Intent.EXTRA_TEXT, textMessage) // само сообщение (ранее сформированно)

        startActivity(intent) // выполнение поручения

    }


}