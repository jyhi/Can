# Timing constraints with one DE1-SoC clock source
# SPDX-FileCopyrightText: 2021 Minyong Li <ml10g20@soton.ac.uk>
# SPDX-License-Identifier: CC0-1.0

create_clock -name CLK_50MHz -period 20 [get_ports {clock}]

# IO ports are currently set to virtual pins and cannot be constrained
# set_false_path -from [get_ports {io_*}] -to [get_ports {io_*}]
