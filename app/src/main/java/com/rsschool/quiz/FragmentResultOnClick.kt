package com.rsschool.quiz

interface FragmentResultOnClick {
    // Получем указание об закрытии или начать заново играть
    fun onClickFragmentResultBack(isClose: Boolean)

    // Посылаем в приложение почты результат
    fun onClickFragmentResultSendEmail()

}