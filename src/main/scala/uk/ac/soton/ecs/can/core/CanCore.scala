// SPDX-FileCopyrightText: 2021 Minyong Li <ml10g20@soton.ac.uk>
// SPDX-License-Identifier: CERN-OHL-W-2.0

package uk.ac.soton.ecs.can.core

import chisel3._
import uk.ac.soton.ecs.can.config.CanCoreConfiguration

class CanCore(implicit cfg: CanCoreConfiguration) extends MultiIOModule {}
