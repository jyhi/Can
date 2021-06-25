package uk.ac.soton.ecs.can.core

import chisel3._

class ControlWord(addrWidth: Int, immWidth: Int = 8) extends Bundle {
  val immediate = UInt(immWidth.W)
  val absoluteBranch = Bool()
  val relativeBranch = Bool()
  val ramAddressRead = Vec(2, UInt(addrWidth.W))
  val ramAddressWrite = UInt(addrWidth.W)
  val fillConstant = Bool()
  val incrementBlockCount = Bool()
  val roundLoop = Bool()
  val addFrom = Bool()
  val xorFrom = Bool()
  val writeBackInit = Bool()
  val writeBackRound = Bool()
}
