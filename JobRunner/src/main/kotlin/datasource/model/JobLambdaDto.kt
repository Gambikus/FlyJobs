package datasource.model

import java.util.UUID

data class JobLambdaDto (
    val id: UUID,
    var job: () -> Unit
)