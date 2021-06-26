package com.rsschool.quiz

interface FragmentQuizOnClick {
    // Создание нового вопроса (следующего или предыдущего), это при нажатии Next или Previous
    fun onClickFragmentQuizNewQuestion(
        index: Int,
        giveNumAnswers: IntArray,
        giveIdAnswers: IntArray,
    )

    // Для формирования результата, получения пройденного в проценте, текст вопроса, текст ответа
    fun onClickFragmentQuizToResult(
        result: Int,
        questions: Array<String>,
        namesDoneAnswers: Array<String>
    )

}