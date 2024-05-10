package pl.edu.agh.gem.internal.client

import pl.edu.agh.gem.model.GroupMembers

interface GroupManagerClient {
    fun getGroupMembers(groupId: String): GroupMembers
}

class GroupManagerClientException(override val message: String?) : RuntimeException()

class RetryableGroupManagerClientException(override val message: String?) : RuntimeException()
