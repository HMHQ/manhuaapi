package com.axlecho.api.bangumi

import com.axlecho.api.module.comic.MHComic
import com.axlecho.api.untils.MHNode
import com.orhanobut.logger.Logger

class BangumiParser {
    companion object {
        private val Tag: String = BangumiParser::javaClass.name
        fun parserComicList(html: String): ArrayList<MHComic> {
            // Logger.v(html)
            val result = ArrayList<MHComic>()

            val body = MHNode(html)
            for (node in body.list("ul#browserItemList > li")) {
                val title = node.text("div.inner > h3 > a.l")
                val cover = node.src("span.image > img.cover" )
                val info = MHComic("-1",title,cover)
                result.add(info)
            }
            return result
        }
    }
}