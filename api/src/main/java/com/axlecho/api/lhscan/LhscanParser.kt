package com.axlecho.api.lhscan

import com.axlecho.api.*
import com.axlecho.api.untils.MHNode
import com.axlecho.api.untils.MHStringUtils
import com.orhanobut.logger.Logger

class LhscanParser {
    companion object {
        private val Tag: String = LhscanParser::javaClass.name

        fun String.filterDigital(): String {
            return this.replace("[^0-9]".toRegex(), "");
        }

        fun String.filterEnd(): String {
            return this.replace(".html", "")
        }


        fun parserComicList(html: String): MHMutiItemResult<MHComicInfo> {
            Logger.v(html)
            val body = MHNode(html)
            val result = ArrayList<MHComicInfo>()
            for (node in body.list("div.media")) {
                Logger.v(node.get().html())
                val title = node.text("div.media-body > h3")
                val gid = node.href("div.media-body > h3 > a").filterEnd()
                val titleJpn = MHConstant.UNKNOWN_TITLE
                val thumb = node.src("img")
                val category = -1
                val posted = MHConstant.UNKNOWN_TIME
                val uploader = MHConstant.UNKNOWN_MAN
                val rating = 0.0f
                val rated = false
                result.add(MHComicInfo(gid, title, titleJpn, thumb, category, posted, uploader, rating, rated, MHApiSource.Lhscan))
            }
            return MHMutiItemResult(result, 1, 1)
        }

        fun parserInfo(html: String, gid: String): MHComicDetail {
            Logger.v(html)
            val body = MHNode(html)

            val title = body.text("ul.manga-info > h1").replace(" - RAW", "")
            val titleJpn = MHConstant.UNKNOWN_TITLE
            val thumb = body.src("img.thumbnail")
            val category = -1
            val posted = MHConstant.UNKNOWN_TIME
            val updateTime = 0L
            val uploader = body.text("ul.manga-info > h1")
            val rating = 0.0f
            val ratingCount = 0
            val rated = ratingCount > 0
            val info = MHComicInfo(gid, title, titleJpn, thumb, category, posted, uploader, rating, rated, MHApiSource.Lhscan)

            val intro = body.text("h3:contains(Description)")
            val favoriteCount = 0
            val isFavorited = false
            val chapters = ArrayList<MHComicChapter>()
            val name = body.text("#about_kit > ul > li:eq(0) > h1")
            for (node in body.list("div#tab-chapper > div > ul > table > tbody > tr")) {
                val chapterTitle = node.text("a.chapter")
                val path = node.href("a.chapter").filterEnd()
                chapters.add(MHComicChapter(chapterTitle.trim { it <= ' ' }, path, MHApiSource.Lhscan))
            }
            val chapterCount = chapters.size
            val comments = ArrayList<MHComicComment>()
            return MHComicDetail(info, intro, chapterCount, favoriteCount, isFavorited, ratingCount, chapters, comments, MHApiSource.Lhscan, updateTime)
        }

        fun parserData(html: String): MHComicData {
            Logger.v(html)
            val body = MHNode(html)
            val list = ArrayList<String>()
            for (node in body.list("img.chapter-img")) {
                list.add(node.src())
            }
            return MHComicData(list, MHApiSource.Lhscan)
        }

        fun parserRaw(html: String): String {
            Logger.v(html)
            val body = MHNode(html)
            val script = body.list("td[valign*=top] > script").first().get().html()
            Logger.v(script)

            val url = MHStringUtils.match("<IMG SRC='(.*?)'>", script, 1)

            return url.replace(Regex("\".*\""), "http://n9.1whour.com/")
        }
    }
}