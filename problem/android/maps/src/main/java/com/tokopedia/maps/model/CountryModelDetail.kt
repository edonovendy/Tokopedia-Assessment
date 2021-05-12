package com.tokopedia.maps.model

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class CountryModelDetail {
    @SerializedName("iso_alpha2")
    @Expose
    var isoAlpha2: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("name_locale")
    @Expose
    var nameLocale: String? = null

    @SerializedName("alternatename")
    @Expose
    var alternatename: List<String>? = null

    @SerializedName("neighbors")
    @Expose
    var neighbors: List<String>? = null

    @SerializedName("postalcode")
    @Expose
    var postalcode: String? = null

    @SerializedName("postalcoderegex")
    @Expose
    var postalcoderegex: String? = null

    @SerializedName("phone")
    @Expose
    var phone: String? = null

    @SerializedName("currencyname")
    @Expose
    var currencyname: String? = null

    @SerializedName("currencycode")
    @Expose
    var currencycode: String? = null

    @SerializedName("tld")
    @Expose
    var tld: String? = null

    @SerializedName("continent")
    @Expose
    var continent: String? = null

    @SerializedName("areainsqkm")
    @Expose
    var areainsqkm: Int? = null

    @SerializedName("population")
    @Expose
    var population: Int? = null

    @SerializedName("capital")
    @Expose
    var capital: String? = null

    @SerializedName("fr_preposition")
    @Expose
    var frPreposition: String? = null

    @SerializedName("fr_article")
    @Expose
    var frArticle: String? = null

    @SerializedName("languages")
    @Expose
    var languages: List<String>? = null

    @SerializedName("latitude")
    @Expose
    var latitude: Int? = null

    @SerializedName("longitude")
    @Expose
    var longitude: Int? = null

    @SerializedName("airportscount")
    @Expose
    var airportscount: Int? = null
}