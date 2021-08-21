// SPDX-FileCopyrightText: 2021 Minyong Li <ml10g20@soton.ac.uk>
// SPDX-License-Identifier: GPL-3.0-or-later

package uk.ac.soton.ecs.can

import chisel3.stage._
import chisel3.util.log2Ceil
import uk.ac.soton.ecs.can.core.CanCore
import uk.ac.soton.ecs.can.config.CanCoreConfiguration

object Main extends App {
  implicit private val coreCfg = CanCoreConfiguration(
    immediateWidth = log2Ceil(128),
    programMemoryWords = 128,
    dataMemoryWords = 16,
    syncReadMemory = true,
    registerFileWords = 2,
    quarterRoundType = 1
  )
  private val stage = new ChiselStage

  stage.execute(
    args,
    Seq(ChiselGeneratorAnnotation(() => new CanCore))
  )
}
