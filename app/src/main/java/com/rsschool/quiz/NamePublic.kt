package com.rsschool.quiz

class NamePublic {
    companion object {
        // Для Bundle
        const val RESULT = "RESULT" // результат
        const val INDEX = "INDEX" // текущий номер (индекс)
        const val GIVE_NUM_ANSWERS = "GIVE_NUM_ANSWERS" // полученные номера ответов
        const val GIVE_ID_ANSWERS = "GIVE_ID_ANSWERS" // полученные id radio кнопок после выбора

        // Для логики формирования авто-размера один раз (через Fragment)
        var cntRunQuiz = -1
    }
}