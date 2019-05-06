package com.axlecho.api.bangumi

import android.text.TextUtils
import com.axlecho.api.*
import com.axlecho.api.bangumi.module.BangumiComicInfo
import com.axlecho.api.bangumi.module.BangumiSearchInfo
import com.axlecho.api.untils.MHNode
import com.orhanobut.logger.Logger
import java.text.SimpleDateFormat
import java.util.*


class BangumiParser {
    companion object {
        private val Tag: String = BangumiParser::javaClass.name

        fun String.filterDigital():String{
           return this.replace("[^0-9]".toRegex(),"");
        }

        fun parserComicList(html: String): ArrayList<MHComicInfo> {
            // Logger.v(html)
            val result = ArrayList<MHComicInfo>()

            val body = MHNode(html)
            for (node in body.list("ul#browserItemList > li")) {
                // Logger.v(node.get().html())
                val gid = node.attr("id").filterDigital().toLong()
                val title = node.text("div.inner > h3 > a.l")
                val titleJpn = node.text("div.inner > h3 > small.grey")
                val thumb = "http:" + node.src("a.subjectCover > span.image > img.cover" )
                val category = -1
                val posted = node.text("div.inner > p.collectInfo > span.tip_j")
                val uploader = node.text("div.inner > p.info")
                val rating = node.attr("p.rateInfo > span.starsinfo","class").filterDigital().toFloat()
                val rated = true
                result.add(MHComicInfo(gid, title, titleJpn, thumb, category, posted?:"", uploader, rating, rated,MHApiSource.Bangumi))
            }
            return result
        }

        fun parserCollectionCount(html:String):Int {
            val body = MHNode(html)
            val countString = body.text("div#headerProfile > div.subjectNav > div.navSubTabsWrapper > ul.navSubTabs > li:eq(2)")
                    .filterDigital()
            // Logger.v(countString)

            if(TextUtils.isDigitsOnly(countString)) {
                return countString.toInt()
            }
            return -1
        }

        fun parserComicComment(html:String):List<MHComicComment> {
            val result = ArrayList<MHComicComment>()
            val body = MHNode(html)
            for (node in body.list("div#comment_box > div.item")) {
                // Logger.v(node.get().html())
                val id = node.attr("a.avatar","href").replace("/user/","")
                var score = 0
                if(node.attr("div.text > span.starsinfo","class") != null) {
                     score = node.attr("div.text > span.starsinfo","class").filterDigital().toInt()
                }
                val time = node.text("div.text > small.grey").replace("@","").trim()
                val user = node.text("div.text > a.l")
                val comment = node.text("div.text > p")
                result.add(MHComicComment(id,score,time, user, comment,MHApiSource.Bangumi))
            }
            return result
        }

        fun parserInfo(info: BangumiComicInfo) : MHComicDetail {
            val gid = info.id
            val title = info.name_cn
            val titleJpn =info.name
            val thumb = info.images.common
            val category = -1
            var posted = ""
            var uploader = ""
            info.staff
            if(info.staff != null && info.staff.isNotEmpty()) {
                 uploader = if(info.staff[0].name_cn.isNotEmpty()) info.staff[0].name_cn else info.staff[0].name
            }

            val rating = info.rating.score / 2.0f
            val ratingCount = info.rating.total
            val rated = ratingCount > 0
            val mhinfo = MHComicInfo(gid, title, titleJpn, thumb, category, posted, uploader, rating, rated,MHApiSource.Bangumi)

            val intro = info.summary
            val chapterCount =0
            val favoriteCount =info.collection.wish + info.collection.collect + info.collection.doing
            val isFavorited = false
            val status = ""
            val chapters = ArrayList<MHComicChapter>()
            val comments = ArrayList<MHComicComment>()
            return MHComicDetail(mhinfo, intro, chapterCount, favoriteCount, isFavorited, ratingCount, chapters, comments,MHApiSource.Bangumi)
        }

        fun parserComicListByApi(info:BangumiSearchInfo) : List<MHComicInfo> {
            val result  = ArrayList<MHComicInfo>()
            for(i in info.list) {
                val gid = i.id
                val title = if(i.name_cn.isNotBlank()) i.name_cn else i.name
                val titleJpn =i.name
                val thumb = i.images.common
                val category = -1
                val posted =i.air_date
                val uploader = ""
                var rating = 0.0f
                var rated = false
                if(i.rating != null) {
                     rating = i.rating.score / 2.0f
                    rated = i.rating.total > 0
                }
                result.add(MHComicInfo(gid,title, titleJpn, thumb, category, posted, uploader, rating, rated,MHApiSource.Bangumi))
            }
            return result
        }
    }
}