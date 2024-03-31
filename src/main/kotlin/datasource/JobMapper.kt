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

        val className =resultSet.getString("class_name")

        val clazz = Class.forName(className)
        // Здесь предполагается, что метод не принимает аргументов
        // Если метод принимает аргументы, необходимо соответствующим образом изменить код ниже
        val method = clazz.getDeclaredMethod(resultSet.getString("method_name"))
        method.isAccessible = true
        val instance = Class.forName(className).newInstance()

        return JobLambdaDto(
            UUID.fromString(resultSet.getString("id"))
        ) { method.invoke(instance) }
    }

    fun kvalueToDto(job: KFunction<Unit>, executeAt: Timestamp,): Job {
        val methodName = job.javaMethod ?: throw IllegalArgumentException("Wrong method")
        // Получаем имя класса
        val className = methodName?.declaringClass?.name ?: throw IllegalArgumentException("Wrong class")



        return Job(id = UUID.randomUUID(), className = className, methodName = methodName.name, executeAt = executeAt)
    }
}