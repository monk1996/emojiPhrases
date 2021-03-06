package com.example.repository

import com.example.module.EmojiPhrases
import com.example.module.User
import com.example.module.Users
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.net.URI

object DataBaseFactory{

    fun init(){
        Database.connect(hikari())
        transaction {
            SchemaUtils.create(EmojiPhrases)
            SchemaUtils.create(Users)


        }
    }
    private fun hikari():HikariDataSource{
        val config=HikariConfig()
        config.driverClassName ="org.postgresql.Driver"
        val uri=URI(System.getenv("DATABASE_URL"))
        val username=uri.userInfo.split(":").toTypedArray()[0]
        val password=uri.userInfo.split(":").toTypedArray()[1]
        config.jdbcUrl="jdbc:postgresql://"+ uri.host+":"+uri.port+uri.path+"?sslmode=require"+"&user=$username&password=$password"
        config.maximumPoolSize=3
        config.isAutoCommit=false
        config.transactionIsolation="TRANSACTION_REPEATABLE_READ"
        config.validate()
        return HikariDataSource(config)

    }
    suspend fun <T> dbQuery(
        block:()->T
    ):T= withContext(Dispatchers.IO){
        transaction { block() }
    }
}