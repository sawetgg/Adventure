package th.ac.nu.adventure



data class Adventure(

        val caption:String,
        val image:String,
        val date:String,
        val time:String,
        val latitude:Double,
        val longitude:Double
){
    constructor() : this("","","","",0.0000000,0.0000000)
}

