package beans

import jakarta.enterprise.context.SessionScoped
import jakarta.inject.Named
import java.io.Serializable

@Named("mapper")
@SessionScoped
class Mapper: Serializable {
    var currentPage = "index"

    fun remap(target: String): String{
        currentPage = target
        return target
    }

    fun getMapping(): String{
        return currentPage
    }
}