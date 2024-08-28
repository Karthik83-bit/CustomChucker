package com.isu.apitracker.data.repository

import com.isu.apitracker.data.model.TransactionData
import com.isu.apitracker.data.srx.TransactionDataDao
import com.isu.apitracker.domain.Repository

class RepositoryImplementation(val dao: TransactionDataDao):Repository {
    override suspend fun getAllApiData(): List<TransactionData> {
        return dao.getAll()
    }

    override suspend fun deleteAllApiData() {
        return dao.delete()
    }

    override suspend fun deleteApiDataWithIds(ids: List<Int>) {
       return dao.deleteUsersByIds(ids)
    }
}