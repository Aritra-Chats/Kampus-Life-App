package com.example.feature_auth

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertNotNull
import org.junit.Test

class AuthUtilsTest {

    // ---------- inferRoleFromEmail ----------

    @Test
    fun `student email with digit prefix returns STUDENT`() {
        assertEquals(UserRole.STUDENT, inferRoleFromEmail("2105123@kiit.ac.in"))
    }

    @Test
    fun `teacher email with letter prefix returns TEACHER`() {
        assertEquals(UserRole.TEACHER, inferRoleFromEmail("john.doe@kiit.ac.in"))
    }

    @Test
    fun `non-KIIT email returns UNKNOWN`() {
        assertEquals(UserRole.UNKNOWN, inferRoleFromEmail("user@gmail.com"))
    }

    @Test
    fun `email is trimmed and lowercased`() {
        assertEquals(UserRole.STUDENT, inferRoleFromEmail("  2105123@KIIT.AC.IN  "))
    }

    @Test
    fun `empty email returns UNKNOWN`() {
        assertEquals(UserRole.UNKNOWN, inferRoleFromEmail(""))
    }

    // ---------- applyRoleOverrides ----------

    @Test
    fun `override email is promoted to TEACHER`() {
        val user = AuthUser(
            displayName = "Test",
            email = "yt.nbt.2812@gmail.com",
            role = UserRole.UNKNOWN
        )
        assertEquals(UserRole.TEACHER, applyRoleOverrides(user).role)
    }

    @Test
    fun `non-override email keeps original role`() {
        val user = AuthUser(
            displayName = "Student",
            email = "2105123@kiit.ac.in",
            role = UserRole.STUDENT
        )
        assertEquals(UserRole.STUDENT, applyRoleOverrides(user).role)
    }

    // ---------- parseKiitEmail ----------

    @Test
    fun `parses valid KIIT student email`() {
        val info = parseKiitEmail("2105123@kiit.ac.in")
        assertNotNull(info)
        assertEquals("2105123", info!!.rollNumber)
        assertEquals(2021, info.admissionYear)
        assertEquals("05", info.schoolCode)
        assertEquals("CSE", info.department)
    }

    @Test
    fun `parses different school code`() {
        val info = parseKiitEmail("2207456@kiit.ac.in")
        assertNotNull(info)
        assertEquals(2022, info!!.admissionYear)
        assertEquals("07", info.schoolCode)
        assertEquals("IT", info.department)
    }

    @Test
    fun `returns null for teacher email`() {
        assertNull(parseKiitEmail("john.doe@kiit.ac.in"))
    }

    @Test
    fun `returns null for non-KIIT email`() {
        assertNull(parseKiitEmail("user@gmail.com"))
    }

    @Test
    fun `returns null for short roll number`() {
        assertNull(parseKiitEmail("123@kiit.ac.in"))
    }

    @Test
    fun `unknown school code gives Unknown department`() {
        val info = parseKiitEmail("2199123@kiit.ac.in")
        assertNotNull(info)
        assertEquals("Unknown", info!!.department)
    }

    // ---------- SCHOOL_CODE_MAP ----------

    @Test
    fun `school code 05 maps to CSE`() {
        assertEquals("CSE", SCHOOL_CODE_MAP["05"])
    }

    @Test
    fun `school code 02 maps to Civil`() {
        assertEquals("Civil", SCHOOL_CODE_MAP["02"])
    }
}
