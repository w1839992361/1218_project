package cn.backed.teriedu.entity.enum

enum class ResourcesType {
    LearnGuide,
    PPTX,
    ClassDesign,
    PremiumCourse,
    ClassRoomExercise,
    Homework,
    TestExam,
    Other;

    fun getCode():Int{
        return ordinal + 1
    }
}