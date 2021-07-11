package uk.ac.soton.ecs.can.types

import chisel3.util.log2Ceil

case object CanCoreALUFunction {
  val blockInitialize = 1
  val columnarRound = 2
  val diagonalRound = 3
  val add = 4
  val xor = 5

  def requiredWidth = log2Ceil(xor)
}
