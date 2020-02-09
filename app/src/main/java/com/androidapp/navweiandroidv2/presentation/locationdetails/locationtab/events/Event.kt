package com.androidapp.navweiandroidv2.presentation.locationdetails.locationtab.events

import com.androidapp.entity.models.Locations

/**
 * Created by S.Nur Uysal on 2019-11-07.
 */

data class OnClickStoreEvent(val locations: Locations)

data class OnClickStoreToMapEvent(val locations: Locations)

data class OnClickOpenOfferFragmentEvent(val selectedMall: Locations,val selectedStore: Locations)