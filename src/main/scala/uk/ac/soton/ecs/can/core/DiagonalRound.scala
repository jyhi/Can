// SPDX-FileCopyrightText: 2021 Minyong Li <ml10g20@soton.ac.uk>
// SPDX-License-Identifier: CERN-OHL-W-2.0

package uk.ac.soton.ecs.can.core

import uk.ac.soton.ecs.can.config.CanCoreConfiguration

class DiagonalRound(implicit cfg: CanCoreConfiguration) extends BaseRound {
  wire(
    Seq(
      Seq(0, 5, 10, 15),
      Seq(1, 6, 11, 12),
      Seq(2, 7, 8, 13),
      Seq(3, 4, 9, 14)
    )
  )
}
