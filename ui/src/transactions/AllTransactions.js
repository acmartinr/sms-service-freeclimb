import React, {Component} from 'react';

import '../common/Common.css'

import CampaignsAPI from '../campaigns/CampaignsAPI';

import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import TablePagination from '@material-ui/core/TablePagination';
import TextField from '@material-ui/core/TextField';

import DateFnsUtils from '@date-io/date-fns';
import {
  MuiPickersUtilsProvider,
  KeyboardDatePicker,
} from '@material-ui/pickers';

class AllTransactions extends Component {
  constructor(props) {
    super(props);

    this.state = {
      selectedTransaction: {},
      transactions: [],
      search: '',
      count: 0,
      rowsPerPage: 25,
      page: 0,
      startDate: new Date(new Date().getTime() - 24 * 60 * 60 * 1000),
      endDate: new Date()
    };

    this.handleChangePage = this.handleChangePage.bind(this);
    this.handleChangeRowsPerPage = this.handleChangeRowsPerPage.bind(this);

    this.handleStartDateChanged = this.handleStartDateChanged.bind(this);
    this.handleEndDateChanged = this.handleEndDateChanged.bind(this);
  }

  componentDidMount() {
    this.updateTransactions();
  }

  handleChangePage(event, value) {
    this.setState({page: value}, this.updateTransactions);
  }

  handleChangeRowsPerPage(event) {
    this.setState({rowsPerPage: event.target.value}, this.updateTransactions);
  }

  handleStartDateChanged(date) {
    var that = this;
    this.setState({startDate: date}, function() {
      that.updateTransactions();
    });
  }
  handleEndDateChanged(date) {
    var that = this;
    this.setState({endDate: date}, function() {
      that.updateTransactions();
    });
  }

  prettyDate(dateMS) {
    if (dateMS > 0) {
      const date = new Date(dateMS);
      return date.toLocaleDateString(date) + ' ' + date.toLocaleTimeString(date);
    } else {
      return 'empty';
    }
  }

  updateTransactions() {
    const request = {
      userId: this.state.userId,
      page: this.state.page,
      startDate: this.state.startDate.getTime(),
      endDate: this.state.endDate.getTime(),
      limit: this.state.rowsPerPage,
      search: this.state.search
    };

    CampaignsAPI.getAllTransactions(request,
    response => {
      if (response.status === "OK") {
        this.setState({
          transactions: response.data.transactions,
          count: response.data.count
        });
      }
    });
  }

  localizedType(type) {
    if (type === 0) { return "Fund is added" }
    if (type === 1) { return "Phone(s) purchased" }
    if (type === 2) { return "Inbound message(s)" }
    if (type === 3) { return "Outbound message(s)" }
    if (type === 4) { return "Phone is renewed" }
    if (type === 5) { return "Carrier surcharge(s)" }
    if (type === 6) { return "Carrier lookup(s)" }

    if (type === 11) { return "Sub users phone(s) purchased" }
    if (type === 12) { return "Sub users inbound message(s)" }
    if (type === 13) { return "Sub users outbound message(s)" }
    if (type === 14) { return "Sub users phone is renewed" }
    if (type === 15) { return "Sub users carrier surcharge(s)" }
    if (type === 16) { return "Sub users carrier lookup(s)" }

    return 'unknown';
  }

  onTextFieldChange(field, event) {
    var that = this;
    this.setState({search: event.target.value}, function() {
      that.updateTransactions();
    });
  }

  render() {
    return (
      <div className='max-width-800'>
        <MuiPickersUtilsProvider utils={DateFnsUtils}>
          <TextField
            margin="normal"
            className="transactions-search-with-button"
            fullWidth
            autoFocus
            name="search"
            label="Enter username or phone number"
            id="search"
            value={this.state.search}
            onChange={(e) => this.onTextFieldChange("search", e)}
          />

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

        <Table>
          <TableHead>
            <TableRow>
              <TableCell>User</TableCell>
              <TableCell>Type</TableCell>
              <TableCell>Amount</TableCell>
              <TableCell>Date</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {this.state.transactions.map(row => (
              <React.Fragment>
                <TableRow key={row.id}>
                  <TableCell>{row.username}</TableCell>
                  <TableCell>{this.localizedType(row.type)}</TableCell>
                  <TableCell>${row.amount.toFixed(2)}</TableCell>
                  <TableCell>{this.prettyDate(row.date)}</TableCell>
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

export default AllTransactions;
