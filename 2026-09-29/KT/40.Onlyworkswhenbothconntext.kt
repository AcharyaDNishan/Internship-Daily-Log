data class User(val name: String)
data class Permissions(val canDeleteAccount: Boolean)
context(user: User, permissions: Permissions)
fun deleteAccount() {
    if (permissions.canDeleteAccount) {
        println("Account for ${user.name} deleted.")
    } else {
        println("Permission denied: ${user.name} cannot delete this account.")
    }
}
fun main() {
    val user = User("Nishan")
    val permissions = Permissions(canDeleteAccount = true)

    with(user) {
        with(permissions) {
            deleteAccount()
        }
    }
}
