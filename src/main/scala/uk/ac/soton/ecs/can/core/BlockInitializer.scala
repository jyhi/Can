// SPDX-FileCopyrightText: 2021 Minyong Li <ml10g20@soton.ac.uk>
// SPDX-License-Identifier: CERN-OHL-W-2.0

package uk.ac.soton.ecs.can.core

import chisel3._
import uk.ac.soton.ecs.can.types.ChaCha20IETFBlock

class BlockInitializer extends MultiIOModule {
  val fillConstants = IO(Input(Bool()))
  val incrementBlockCount = IO(Input(Bool()))
  val in = IO(Input(UInt(512.W)))
  val out = IO(Output(UInt(512.W)))

  private val _in = in.asTypeOf(new ChaCha20IETFBlock)
  private val _out = Wire(new ChaCha20IETFBlock)
  out := _out.asUInt()

  private val constant = "h617078653320646e79622d326b206574".U(128.W)

  _out.constant := Mux(fillConstants, constant, _in.constant)
  _out.key := _in.key
  _out.blockCount := Mux(
    incrementBlockCount,
    _in.blockCount + 1.U,
    _in.blockCount
  )
  _out.nonce := _in.nonce
}
