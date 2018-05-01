package com.w11k.psql

import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import java.sql.PreparedStatement

class PsqlWorker(val dataSource: HikariDataSource, doMigrate: Boolean = false) {

    init {
        if (doMigrate) {
            val f = Flyway()
            f.setDataSource(dataSource.jdbcUrl, dataSource.username, dataSource.password)
            f.migrate()
        }
    }

    fun getPersons(): List<Person> {
        val rs = dataSource.connection.prepareStatement("SELECT * FROM person").executeQuery()
        return rs.use {
            generateSequence {
                if (rs.next())
                    Person(
                            id = rs.getInt(1),
                            firstName = rs.getString(2),
                            lastName = rs.getString(3)
                    )
                else
                    null
            }.toList()
        }
    }

    fun insertPerson(person: Person): Unit {
        val ps: PreparedStatement = dataSource.connection.prepareStatement("INSERT INTO person (first_name, last_name) VALUES (?,?)")
        ps.setString(1, person.firstName)
        ps.setString(2, person.lastName)
        ps.execute()
    }
}

data class Person(val id: Int = Int.MIN_VALUE, val firstName: String, val lastName: String)