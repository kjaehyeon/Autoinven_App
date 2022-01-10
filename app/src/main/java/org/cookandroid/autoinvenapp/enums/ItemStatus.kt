package org.cookandroid.autoinvenapp.enums


enum class ItemStatus(val code : Int, val description : String) {
    BEFORE_RECEIVING(0,"입고전"),
    RECEIVED(1,"입고"),
    RELEASED(2,"출고");
}
fun getItemStatusFromInt(value : Int) : ItemStatus{
    return when(value){
        0 -> ItemStatus.BEFORE_RECEIVING
        1 -> ItemStatus.RECEIVED
        2 -> ItemStatus.RELEASED
        else -> throw IllegalStateException("invalid value is passed to getItemStatusFromInt() fun")
    }
}