package com.axlecho.api.lhscan

import com.axlecho.api.Api
import com.axlecho.api.MHCategory

class LhscanCategory(_api: Api) : MHCategory(_api) {
    override fun loadCategory(): String = ""

    override fun loadTime(): String = ""

    override fun saveTime(time: String) {}

    override fun saveCategory(category: String) {}

    override fun build(): String = ""

}