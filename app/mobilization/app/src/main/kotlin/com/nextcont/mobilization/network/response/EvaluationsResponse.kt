package com.nextcont.mobilization.network.response

import com.nextcont.mobilization.model.Evaluation

data class EvaluationsResponse(
    val evaluations: List<Evaluation>
)