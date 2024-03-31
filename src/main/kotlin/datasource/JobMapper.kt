package datasource

import datasource.model.JobLambdaDto
import datasource.model.Job
import java.sql.ResultSet
import java.sql.Timestamp
import java.util.UUID
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.javaMethod
import kotlin.reflect.jvm.reflect

class JobMapper {

    fun dtoToLambdaJob(resultSet: ResultSet): JobLambdaDto {

        val clazz = Class.forName(resultSet.getString("class_name"))

        // Получаем объект метода (для метода без параметров не указываем параметры)
        // Замените arrayOf(String::class.java, Int::class.java) параметрами Вашего метода соответственно
        val method = clazz.getDeclaredMethod(resultSet.getString("method_name"))

        return JobLambdaDto(
            UUID.fromString(resultSet.getString("id"))
        ) { method.invoke(null) }
    }

    fun kvalueToDto(job: KFunction<Unit>, executeAt: Timestamp,): Job {
        val className = job.reflect()?.javaMethod?.declaringClass?.name ?: throw IllegalArgumentException("Wrong class")
        val methodName = job.name



        return Job(id = UUID.randomUUID(), className = className, methodName = methodName, executeAt = executeAt)
    }
}