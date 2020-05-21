package com.nextcont.sample.mobilization.model

import com.nextcont.sample.mobilization.Mobilization
import org.dizitart.no2.IndexType
import org.dizitart.no2.objects.Id
import org.dizitart.no2.objects.Index
import org.dizitart.no2.objects.Indices
import org.dizitart.no2.objects.filters.ObjectFilters.eq

@Indices(Index(value = "username", type = IndexType.Fulltext))
class Account {

    @Id lateinit var id: String
    lateinit var username: String
    @Transient lateinit var password: String
    lateinit var fullName: String
    lateinit var gender: Gender
    var age: Int = 0
    lateinit var idCard: String
    lateinit var role: Role
    val createAt = System.currentTimeMillis()

    enum class Gender {
        Male,
        Female;

        companion object {

            fun from(code: Int): Gender {
                return if (code == 0) Male else Female
            }

        }

    }

    enum class Role {
        Sergeant,
        Soldier;

        companion object {

            fun from(code: Int): Role {
                return if (code == 0) Sergeant else Soldier
            }
        }

    }

    companion object {
        fun find(username: String): Account? {
            val repo = Mobilization.Store.getRepository(Account::class.java)
            return repo.find(eq("username", username)).singleOrNull()
        }
    }

    fun saveOrUpdate() {
        val repo = Mobilization.Store.getRepository(Account::class.java)
        repo.update(this, true)
    }


}