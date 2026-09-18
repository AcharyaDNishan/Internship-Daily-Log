fun main() {
    val morningNotification = 51
    val eveningNotification = 135
    
    printNotificationSummary(morningNotification)
    printNotificationSummary(eveningNotification)
}


fun printNotificationSummary(nom: Int) {
    // Fill in the code.
  if(nom>99){
    println("Your phone is blowing up! You have 99+ notifications.")
  } else {
    println("You have $nom notifications.")
  }
}
