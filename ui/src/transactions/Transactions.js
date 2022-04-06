import React, {Component} from 'react';

import '../common/Common.css'

import CampaignsAPI from '../campaigns/CampaignsAPI';
import Session from '../common/Session';

import Button from '@material-ui/core/Button';

import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import TablePagination from '@material-ui/core/TablePagination';

import DateFnsUtils from '@date-io/date-fns';
import {
  MuiPickersUtilsProvider,
  KeyboardDatePicker,
} from '@material-ui/pickers';

class Transactions extends Component {
  constructor(props) {
    super(props);

    this.state = {
      selectedTransaction: {},
      transactions: [],
      count: 0,
      rowsPerPage: 25,
      page: 0,
      startDate: new Date(new Date().getTime() - 30 * 24 * 60 * 60 * 1000),
      endDate: new Date(),
      statistics: [],
      dailyStatistics: [],
      statisticsReceived: false,
      showDailyTransactions: false
    };

    this.handleChangePage = this.handleChangePage.bind(this);
    this.handleChangeRowsPerPage = this.handleChangeRowsPerPage.bind(this);

    this.handleStartDateChanged = this.handleStartDateChanged.bind(this);
    this.handleEndDateChanged = this.handleEndDateChanged.bind(this);
  }

  componentDidMount() {
    this.updateTransactions();
    //this.updateDailyStatistics();
  }

  handleChangePage(event, value) {
    this.setState({page: value}, this.updateTransactions);
  }

  handleChangeRowsPerPage(event) {
    this.setState({rowsPerPage: event.target.value}, this.updateTransactions);
  }

  handleStartDateChanged(date) {
    this.setState({startDate: date});
  }

  handleEndDateChanged(date) {
    this.setState({endDate: date});
  }

  prettyDate(dateMS, type) {
    if (dateMS > 0) {
      const date = new Date(dateMS);
      if (type > 1) {
        return date.toLocaleDateString(date);
      } else {
        return date.toLocaleDateString(date) + ' ' + date.toLocaleTimeString(date);
      }
    } else {
      return 'empty';
    }
  }

  isAdmin() {
    return Session.getUser().role === 0;
  }

  updateTransactions() {
    const request = {
      userId: Session.getUser().id,
      page: this.state.page,
      limit: this.state.rowsPerPage,
      search: this.state.search
    };

    CampaignsAPI.getTransactions(request,
      response => {
        if (response.status === "OK") {
          this.setState({
            transactions: response.data.transactions,
            count: response.data.count
          });
        }
      });
  }

  updateDailyStatistics() {
    const request = {
      userId: Session.getUser().id
    };

    CampaignsAPI.getDailyStatistics(request,
      response => {
        if (response.status === "OK") {
          var result = [];

          for (var prop in response.data) {
            result.push({title: prop, value: response.data[prop]});
          }

          this.setState({
            dailyStatistics: result
          });
        }
      });
  }

  requestStatistics() {
    const request = {
      userId: Session.getUser().id,
      startDate: this.state.startDate.getTime(),
      endDate: this.state.endDate.getTime()
    };

    CampaignsAPI.getTransactionsStatistics(request,
      response => {
        if (response.status === "OK") {
          this.setState({
            statistics: response.data,
            statisticsReceived: true
          });
        }
      });
  }

  localizedType(type) {
    if (type === 0) {
      return "Fund is added"
    }
    if (type === 1) {
      return "Phone purchased"
    }
    if (type === 2) {
      return "Inbound message"
    }
    if (type === 3) {
      return "Outbound message"
    }
    if (type === 4) {
      return "Phone is renewed"
    }
    if (type === 5) {
      return "Carrier surcharge"
    }
    if (type === 6) {
      return "Carrier lookup"
    }

    if (type === 11) {
      return "Sub users phone purchased"
    }
    if (type === 12) {
      return "Sub users inbound message"
    }
    if (type === 13) {
      return "Sub users outbound message"
    }
    if (type === 14) {
      return "Sub users phone is renewed"
    }
    if (type === 15) {
      return "Sub users carrier surcharge"
    }
    if (type === 16) {
      return "Sub users carrier lookup"
    }

    return 'unknown';
  }

  calculateTotalAmount() {
    var result = 0;
    for (var i = 0; i < this.state.statistics.length; i++) {
      result = result + this.state.statistics[i].amount;
    }

    return result;
  }

  generateInOutRatio(row) {
    var inbound = 0;
    var outbound = 0;

    for (var i = 0; i < this.state.transactions.length; i++) {
      var transaction = this.state.transactions[i];
      if (transaction.date === row.date) {
        if (transaction.type === 2) {
          inbound = parseFloat(transaction.details.split("count: ")[1]);
        } else if (transaction.type === 3) {
          outbound = parseFloat(transaction.details.split("count: ")[1]);
        }
      }
    }

    if (outbound === 0) {
      return "-";
    } else {
      return (inbound / outbound * 100).toFixed(2) + "%";
    }
  }

  render() {
    return (
      <div className='max-width-800'>
        {this.isAdmin() && <div>
          <label className='transactions-title'>Users transactions statistics:</label>

          <MuiPickersUtilsProvider utils={DateFnsUtils}>
            <KeyboardDatePicker
              disableToolbar
              variant="inline"
              className="transactions-date-picker inline padding-right-10"
              format="MM/dd/yyyy"
              margin="normal"
              id="date-picker-inline"
              label="Start date"
              value={this.state.startDate}
              onChange={this.handleStartDateChanged}
              KeyboardButtonProps={{
                'aria-label': 'change date',
              }}/>

            <KeyboardDatePicker
              disableToolbar
              variant="inline"
              className="transactions-date-picker inline padding-right-10"
              format="MM/dd/yyyy"
              margin="normal"
              id="date-picker-inline"
              label="End date"
              value={this.state.endDate}
              onChange={this.handleEndDateChanged}
              KeyboardButtonProps={{
                'aria-label': 'change date',
              }}/>

          </MuiPickersUtilsProvider>

          <Button className='inline margin-top-30'
                  onClick={(e) => this.requestStatistics()} color="primary">
            Get Statistics
          </Button>

          {this.state.statisticsReceived &&
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Type</TableCell>
                <TableCell>Count</TableCell>
                <TableCell>Amount</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {this.state.statistics.map(row => (
                <TableRow key={row.id}>
                  <TableCell>{this.localizedType(row.type)}</TableCell>
                  <TableCell>{row.count}</TableCell>
                  <TableCell>${row.amount.toFixed(2)}</TableCell>
                </TableRow>
              ))}
              <TableRow>
                <TableCell>Total</TableCell>
                <TableCell></TableCell>
                <TableCell>${this.calculateTotalAmount().toFixed(2)}</TableCell>
              </TableRow>
            </TableBody>
          </Table>
          }
        </div>}

        {this.state.showDailyTransactions &&
        <React.Fragment>
          <label className='transactions-title'>Daily in/out statistics:</label>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Type</TableCell>
                <TableCell>Count</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {this.state.dailyStatistics.map(row => (
                <TableRow key={row.title}>
                  <TableCell>{row.title}</TableCell>
                  <TableCell>{row.value}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </React.Fragment>}

        <label className='transactions-title'>Your transactions:</label>

        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Type</TableCell>
              <TableCell>Details</TableCell>
              <TableCell>Amount</TableCell>
              <TableCell>Date</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {this.state.transactions.map(row => (
              <React.Fragment>
                {row.type === 2 &&
                <TableRow key={"ratio_" + row.id}>
                  <TableCell>In/Out Ratio</TableCell>
                  <TableCell></TableCell>
                  <TableCell>{this.generateInOutRatio(row)}</TableCell>
                  <TableCell>{this.prettyDate(row.date, row.type)}</TableCell>
                </TableRow>
                }
                <TableRow key={row.id}>
                  <TableCell>{this.localizedType(row.type)}</TableCell>
                  <TableCell>{row.details}</TableCell>
                  <TableCell>${row.amount.toFixed(2)}</TableCell>
                  <TableCell>{this.prettyDate(row.date, row.type)}</TableCell>
                </TableRow>
              </React.Fragment>
            ))}
          </TableBody>
        </Table>

        <TablePagination
          rowsPerPageOptions={[25, 50]}
          component="div"
          count={this.state.count}
          rowsPerPage={this.state.rowsPerPage}
          page={this.state.page}
          backIconButtonProps={{
            'aria-label': 'Previous Page',
          }}
          nextIconButtonProps={{
            'aria-label': 'Next Page',
          }}
          onChangePage={this.handleChangePage}
          onChangeRowsPerPage={this.handleChangeRowsPerPage}
        />

      </div>
    )
  }
}

export default Transactions;
