Feature: แก้ไขข้อมูลหอพัก
    As a ผู้จัดการหอพัก I want to แก้ไขข้อมูลหอพัก และกำหนดวันชำระเงินประจำเดือนได้

Background:
    Given a หอพักมีชี่อหอพักเป็น -ใส่ชื่อหอพัก- และวันชำระเงินประจำเดือนเป็นวันที่ 1

Scenario:
    When กดแก้ไขข้อมูลหอพัก จากนั้นใส่ชื่อหอพักเป็น หอพักสุขใจ และวันชำระเงินประจำเดือนเป็นวันที่ 15 แล้วกดตกลง
    Then ชื่อหอพักคือ หอพักสุขใจ และวันชำระเงินประจำเดือนคือวันที่ 15