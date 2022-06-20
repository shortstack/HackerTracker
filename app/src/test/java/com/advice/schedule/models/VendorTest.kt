package com.advice.schedule.models

import com.shortstack.hackertracker.models.firebase.FirebaseVendor
import com.shortstack.hackertracker.toVendor
import org.junit.Assert.assertEquals
import org.junit.Test


class VendorTest {

    @Test
    fun toVendorTitleDescription() {
        val firebase = FirebaseVendor(name = "Vendor", description = "Description")

        val vendor = firebase.toVendor()

        assertEquals("Vendor", vendor.name)
        assertEquals("Description", vendor.summary)
        assertEquals(false, vendor.partner)
        assertEquals(null, vendor.link)
    }

    @Test
    fun toVendorNullDescription() {
        val firebase = FirebaseVendor(name = "Vendor")

        val vendor = firebase.toVendor()

        assertEquals("Vendor", vendor.name)
        assertEquals("Nothing to say.", vendor.summary)
    }

    @Test
    fun toVendorPartner() {
        val firebase = FirebaseVendor(name = "Vendor", partner = true)

        val vendor = firebase.toVendor()

        assertEquals("Vendor", vendor.name)
        assertEquals(true, vendor.partner)
    }


    @Test
    fun toVendorLink() {
        val firebase = FirebaseVendor(name = "Vendor", link = "google.ca")

        val vendor = firebase.toVendor()

        assertEquals("Vendor", vendor.name)
        assertEquals("google.ca", vendor.link)
    }
}