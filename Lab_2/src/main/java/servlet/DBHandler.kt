package servlet

import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.SQLException

class DBHandler private constructor() {

    private val databaseHostPort = "jdbc:postgresql://pg:5432"
    private val databaseName = "studs"
    private var conn: Connection
    private val sqlCheck = "SELECT * from users where login = ? AND pass = ?;"
    private val sqlReg = "INSERT into users(login, pass) values(?, ?);"

    init {
        conn = DriverManager.getConnection(
            "$databaseHostPort/$databaseName",
            "DB LOGIN",
            "DB PASS")
    }

    fun getUserAuth(login: String?, pass: String?): Boolean {
        return try {
            val st: PreparedStatement = conn.prepareStatement(sqlCheck)
            st.setString(1, login)
            st.setString(2, pass)
            st.executeQuery().next()
        } catch (e: SQLException) {
            false
        }
    }

    fun registerUser(login: String?, pass: String?): Boolean {
        return try {
            val st: PreparedStatement = conn.prepareStatement(sqlReg)
            st.setString(1, login)
            st.setString(2, pass)
            st.executeUpdate() > 0
        } catch (e: SQLException) {
            false
        }
    }

    companion object {
        var instance: DBHandler? = null
            get() {
                if (field == null) field = DBHandler()
                return field
            }
            private set
    }
}