import React, {Component} from 'react';

import './Payments.css';
import '../common/Common.css'

import CampaignsAPI from '../campaigns/CampaignsAPI';
import Session from '../common/Session';

import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import TablePagination from '@material-ui/core/TablePagination';

class Payments extends Component {
  constructor(props) {
    super(props);

    this.state = {
      payments: [],
      count: 0,
      rowsPerPage: 10,
      page: 0
    };

    this.updatePayments = this.updatePayments.bind(this);

    this.handleChangePage = this.handleChangePage.bind(this);
    this.handleChangeRowsPerPage = this.handleChangeRowsPerPage.bind(this);
  }

  componentDidMount() {
    this.updatePayments();
  }

  handleChangePage(event, value) {
    this.setState({page: value}, this.updatePayments);
  }

  handleChangeRowsPerPage(event) {
    this.setState({rowsPerPage: event.target.value}, this.updatePayments);
  }

  prettyDate(dateMS) {
    if (dateMS > 0) {
      const date = new Date(dateMS);
      return date.toLocaleDateString(date) + ' ' + date.toLocaleTimeString(date);
    } else {
      return 'empty';
    }
  }

  updatePayments() {
    const request = {
      userId: Session.getUser().id,
      page: this.state.page,
      limit: this.state.rowsPerPage,
      search: this.state.search
    };

    CampaignsAPI.getPayments(request,
    response => {
      if (response.status === "OK") {
        this.setState({
          payments: response.data.payments,
          count: response.data.count
        });
      }
    });
  }

  render() {
    return (
      <div className='max-width-800'>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Business name</TableCell>
              <TableCell>Amount</TableCell>
              <TableCell className='table-date-column'>Date</TableCell>
              <TableCell>Details</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {this.state.payments.map(row => (
              <TableRow key={row.date}>
                <TableCell>{row.username}</TableCell>
                <TableCell>${row.amount}</TableCell>
                <TableCell>{this.prettyDate(row.date)}</TableCell>
                <TableCell>{row.details}</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>

        <TablePagination
          rowsPerPageOptions={[10, 25, 50]}
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

export default Payments;
